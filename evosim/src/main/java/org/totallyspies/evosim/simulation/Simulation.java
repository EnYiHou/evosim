package org.totallyspies.evosim.simulation;

import lombok.Getter;
import lombok.Setter;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.entities.Prey;
import org.totallyspies.evosim.fxml.MainController;
import org.totallyspies.evosim.geometry.Coordinate;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.NamedThreadFactory;
import org.totallyspies.evosim.utils.ReadWriteLockedItem;
import org.totallyspies.evosim.utils.Rng;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * The class in which the bulk of the simulation loop is managed.
 *
 * @author mattlep11, EnYi
 */
public final class Simulation {


    /**
     * List of all simulation created until now to shutdown cleanly.
     */

    private static final LinkedList<Simulation> SIMULATIONS = new LinkedList<>();

    /**
     * Shuts down all instantiated simulations.
     */
    public static void shutdownAll() {
        SIMULATIONS.forEach(Simulation::shutdown);
    }

    /**
     * The width of the whole map.
     */
    public static final int MAP_SIZE_X = 20;

    /**
     * The height of the whole map.
     */
    public static final int MAP_SIZE_Y = 20;

    /**
     * The x and y size of a single grid.
     */
    public static final int GRID_SIZE = 250;

    /**
     * Grids of entities.
     */
    @Getter
    private final ReadWriteLockedItem<List<Entity>>[][] entityGrids;

    /**
     * Entities to add on every update.
     */
    private final ReadWriteLockedItem<List<Entity>>[][] updateToAdd;

    /**
     * Entities to remove on every update.
     */
    private final ReadWriteLockedItem<List<Entity>>[][] updateToRemove;

    /**
     * Service to create updates.
     */
    private final ScheduledExecutorService updateService;

    /**
     * Current update being executed.
     */
    private ScheduledFuture<?> currentUpdate;

    /**
     * The number of prey alive in the simulation.
     */
    @Getter
    @Setter
    private int preyCount;

    /**
     * The number of predators alive in the simulation.
     */
    @Getter
    @Setter
    private int predatorCount;

    /**
     * Executor service that controls all the threads calculating collisions.
     */
    private final ExecutorService collisionCheckerService;

    /**
     * Constructs a new simulation based on the default configuration.
     */
    public Simulation() {
        this.entityGrids = new ReadWriteLockedItem[MAP_SIZE_X][MAP_SIZE_Y];
        this.updateToAdd = new ReadWriteLockedItem[MAP_SIZE_X][MAP_SIZE_Y];
        this.updateToRemove = new ReadWriteLockedItem[MAP_SIZE_X][MAP_SIZE_Y];

        for (int i = 0; i < this.entityGrids.length; ++i) {
            for (int j = 0; j < this.entityGrids[i].length; ++j) {
                this.entityGrids[i][j] = new ReadWriteLockedItem<>(new LinkedList<>());
                this.updateToAdd[i][j] = new ReadWriteLockedItem<>(new LinkedList<>());
                this.updateToRemove[i][j] = new ReadWriteLockedItem<>(new LinkedList<>());
            }
        }

        this.collisionCheckerService = Executors.newFixedThreadPool(
                12,
                new NamedThreadFactory("collision")
        );

        this.populateEntityList(
                Configuration.getConfiguration().getPreyInitialPopulation(),
                Configuration.getConfiguration().getPredatorInitialPopulation()
        );

        this.updateService = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("update")
        );

        SIMULATIONS.add(this);
    }

    /**
     * Populates the entity list by constructing all initial entities based on user given initial
     * populations.
     *
     * @param initPrey     the initial number of prey spawned
     * @param initPredator the initial number of predators spawned
     */
    private void populateEntityList(final int initPrey, final int initPredator) {
        final double maxSpeed = Configuration.getConfiguration().getEntityMaxSpeed();
        final double minSpeed = Configuration.getConfiguration().getEntityMinSpeed();

        final Consumer<Entity> addToGrid = entity -> {
            final Coordinate coord = pointToGridCoord(entity.getBodyCenter());

            final ReadWriteLockedItem<List<Entity>> chunk =
                    this.entityGrids[coord.getX()][coord.getY()];

            chunk.writeLock().lock();
            try {
                chunk.get().add(entity);
            } finally {
                chunk.writeLock().unlock();
            }
        };

        for (int i = 0; i < initPrey; i++) {
            addToGrid.accept(new Prey(
                    Rng.RNG.nextDouble(minSpeed, maxSpeed),
                    new Point(
                            Rng.RNG.nextDouble(0, MAP_SIZE_X * GRID_SIZE),
                            Rng.RNG.nextDouble(0, MAP_SIZE_Y * GRID_SIZE)
                    ),
                    Rng.RNG.nextDouble(0, 2 * Math.PI), 0L
            ));
        }

        for (int i = 0; i < initPredator; i++) {
            addToGrid.accept(new Predator(
                    Rng.RNG.nextDouble(minSpeed, maxSpeed),
                    new Point(
                            Rng.RNG.nextDouble(0, MAP_SIZE_X * GRID_SIZE),
                            Rng.RNG.nextDouble(0, MAP_SIZE_Y * GRID_SIZE)
                    ), Rng.RNG.nextDouble(0, 2 * Math.PI), 0L));
        }

        this.preyCount += initPrey;
        this.predatorCount += initPredator;
    }

    private void checkCollisions(final Entity a, final Entity b) {
        if (a.collidesWith(b)) {
            a.onCollide(b);
            b.onCollide(a);
        }
    }


    private void update() {
        IntStream.range(0, MAP_SIZE_X * MAP_SIZE_Y).parallel().forEach(
                (chunkIndex) -> {
                    final Coordinate chunkCoord = new Coordinate(
                            chunkIndex % MAP_SIZE_X,
                            chunkIndex / MAP_SIZE_X
                    );

                    final ReadWriteLockedItem<List<Entity>> chunk =
                            this.entityGrids[chunkCoord.getX()][chunkCoord.getY()];

                    chunk.readLock().lock();
                    try {
                        final int entityCount = chunk.get().size();
                        IntStream.range(0, entityCount).parallel().forEach((i) -> {
                            final Entity entity = chunk.get().get(i);

                            final Coordinate oldCoord = pointToGridCoord(entity.getBodyCenter());

                            entity.update();
                            if (entity.isDead()) {
                                if (entity instanceof Prey) {
                                    --this.preyCount;
                                } else if (entity instanceof Predator) {
                                    --this.predatorCount;
                                }

                                final ReadWriteLockedItem<List<Entity>> chk =
                                        this.updateToRemove[oldCoord.getX()][oldCoord.getY()];

                                chk.writeLock().lock();
                                try {
                                    chk.get().add(entity);
                                } finally {
                                    chk.writeLock().unlock();
                                }

                                return;
                            } else if (entity.isSplit()) {
                                final ReadWriteLockedItem<List<Entity>> chk =
                                        this.updateToAdd[oldCoord.getX()][oldCoord.getY()];

                                chk.writeLock().lock();
                                try {
                                    chk.get().add(entity.clone(MainController.getController()
                                            .getTimerProperty().getValue().getSeconds()));
                                } finally {
                                    chk.writeLock().unlock();
                                }

                                entity.setSplitEnergy(0);
                                entity.setChildCount(entity.getChildCount() + 1);
                                entity.setSplit(false);

                                if (entity instanceof Prey) {
                                    ++this.preyCount;
                                } else if (entity instanceof Predator) {
                                    ++this.predatorCount;
                                }
                            }

                            final Coordinate curCoord = pointToGridCoord(entity.getBodyCenter());

                            if (!curCoord.equals(oldCoord)) {
                                final ReadWriteLockedItem<List<Entity>> chkFrom =
                                        this.updateToRemove[oldCoord.getX()][oldCoord.getY()];

                                chkFrom.writeLock().lock();
                                try {
                                    chkFrom.get().add(entity);
                                } finally {
                                    chkFrom.writeLock().unlock();
                                }

                                final ReadWriteLockedItem<List<Entity>> chkTo =
                                        this.updateToAdd[curCoord.getX()][curCoord.getY()];

                                chkTo.writeLock().lock();
                                try {
                                    chkTo.get().add(entity);
                                } finally {
                                    chkTo.writeLock().unlock();
                                }
                            }

                            IntStream.range(i + 1, entityCount).parallel().forEach(
                                    (j) -> {
                                        final Entity other = chunk.get().get(j);

                                        this.collisionCheckerService.execute(
                                                () -> this.checkCollisions(entity, other)
                                        );
                                    }
                            );
                        });
                    } finally {
                        chunk.readLock().unlock();
                    }
                }
        );

        for (int i = 0; i < MAP_SIZE_X; ++i) {
            for (int j = 0; j < MAP_SIZE_Y; ++j) {
                final ReadWriteLockedItem<List<Entity>> chunk = this.entityGrids[i][j];
                final ReadWriteLockedItem<List<Entity>> toRemove = this.updateToRemove[i][j];
                final ReadWriteLockedItem<List<Entity>> toAdd = this.updateToAdd[i][j];

                chunk.writeLock().lock();
                try {
                    toRemove.writeLock().lock();
                    try {
                        chunk.get().removeAll(toRemove.get());
                        toRemove.get().clear();
                    } finally {
                        toRemove.writeLock().unlock();
                    }

                    toAdd.writeLock().lock();
                    try {
                        chunk.get().addAll(toAdd.get());
                        toAdd.get().clear();
                    } finally {
                        toAdd.writeLock().unlock();
                    }
                } finally {
                    chunk.writeLock().unlock();
                }
            }
        }
    }

    /**
     * Runs a function on all entities in a grid.
     *
     * @param x X position of grid
     * @param y Y position of grid
     * @param r Function to map on the entities
     */
    public void forEachGridEntities(final int x, final int y, final Consumer<Entity> r) {
        final ReadWriteLockedItem<List<Entity>> chunk = this.entityGrids[x][y];

        chunk.readLock().lock();

        try {
            chunk.get().forEach(r);
        } finally {
            chunk.readLock().unlock();
        }
    }

    /**
     * Tests if a point is within the map.
     *
     * @param point The point to check
     * @return If the point is in the map
     */
    public static boolean isPointValid(final Point point) {
        return (
                0 <= point.getX() && point.getX() < Simulation.MAP_SIZE_X * GRID_SIZE
                        && 0 <= point.getY() && point.getY() < Simulation.MAP_SIZE_Y * GRID_SIZE
        );
    }

    /**
     * Converts a point to the grid coordinate it is in.
     *
     * @param point THe point to be converted.
     * @return The coordinate of the grid containing the point.
     */
    public static Coordinate pointToGridCoord(final Point point) {
        return new Coordinate(
                (int) (point.getX() / GRID_SIZE) % MAP_SIZE_X,
                (int) (point.getY() / GRID_SIZE) / MAP_SIZE_Y
        );
    }

    /**
     * Starts updating the simulation.
     */
    public void playUpdate() {
        if (this.currentUpdate != null) {
            return;
        }

        this.currentUpdate = this.updateService.scheduleAtFixedRate(
                this::update, 0, 16666666, TimeUnit.NANOSECONDS
        );
    }

    /**
     * Pauses updating the simulation.
     */
    public void pauseUpdate() {
        if (this.currentUpdate != null) {
            this.currentUpdate.cancel(false);
            this.currentUpdate = null;
        }
    }

    /**
     * Kills the simulation. Cannot be restarted after.
     */
    public void shutdown() {
        this.pauseUpdate();
        this.updateService.shutdown();
        this.collisionCheckerService.shutdown();

        try {
            this.updateService.awaitTermination(1, TimeUnit.SECONDS);
            this.collisionCheckerService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            this.updateService.shutdownNow();
            this.collisionCheckerService.shutdownNow();
        }
    }
}
