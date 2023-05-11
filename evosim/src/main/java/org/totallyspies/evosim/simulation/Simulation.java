package org.totallyspies.evosim.simulation;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.entities.Prey;
import org.totallyspies.evosim.geometry.Coordinate;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.ui.EvosimApplication;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.EvosimException;
import org.totallyspies.evosim.utils.NamedThreadFactory;
import org.totallyspies.evosim.utils.ReadWriteLockedItem;
import org.totallyspies.evosim.utils.Rng;

import java.util.List;

/**
 * The class in which the bulk of the simulation loop is managed.
 *
 * @author mattlep11, EnYi
 */
public final class Simulation {


    /**
     * Nanoseconds to wait between each update. Defaults to 60 per second.
     */
    private static final long UPDATE_INTERVAL_NANO = 16666666;

    /**
     * X map size of this simulation.
     */
    @Getter
    private final int mapSizeX;

    /**
     * Y map size of this simulation.
     */
    @Getter
    private final int mapSizeY;

    /**
     * Grid size of this simulation.
     */
    @Getter
    private final int gridSize;

    /**
     * Number of collision threads to create.
     */
    private static final int COLLISION_THREAD_COUNT = 12;

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
     * Constructs a new simulation with given size.
     * @param newMapSizeX X map size to use for the simulation.
     * @param newMapSizeY Y map size to use for the simulation.
     * @param newGridSize Grid size to use for the simulation.
     * @param shouldPopulate Whether the simulation should be initialized with random values.
     */
    public Simulation(
        final int newMapSizeX,
        final int newMapSizeY,
        final int newGridSize,
        final boolean shouldPopulate
    ) throws EvosimException {
        this.mapSizeX = newMapSizeX;
        this.mapSizeY = newMapSizeY;
        this.gridSize = newGridSize;

        this.entityGrids = new ReadWriteLockedItem[this.mapSizeX][this.mapSizeY];
        this.updateToAdd = new ReadWriteLockedItem[this.mapSizeX][this.mapSizeY];
        this.updateToRemove = new ReadWriteLockedItem[this.mapSizeX][this.mapSizeY];

        for (int i = 0; i < this.entityGrids.length; ++i) {
            for (int j = 0; j < this.entityGrids[i].length; ++j) {
                this.entityGrids[i][j] = new ReadWriteLockedItem<>(new LinkedList<>());
                this.updateToAdd[i][j] = new ReadWriteLockedItem<>(new LinkedList<>());
                this.updateToRemove[i][j] = new ReadWriteLockedItem<>(new LinkedList<>());
            }
        }

        this.collisionCheckerService = Executors.newFixedThreadPool(
            COLLISION_THREAD_COUNT,
            new NamedThreadFactory("collision")
        );

        this.updateService = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("update")
        );

        if (shouldPopulate) {
            this.defaultPopulateEntityList();
        }

        EvosimApplication.getApplication().getShutdownHooks().add(this::shutdown);
    }

    /**
     * Populates the entity list by constructing all initial entities based on user given initial
     * populations.
     */
    private void defaultPopulateEntityList() throws EvosimException {
        final double maxSpeed = Configuration.getConfiguration().getEntityMaxSpeed();
        final double minSpeed = Configuration.getConfiguration().getEntityMinSpeed();
        final int initPrey = Configuration.getConfiguration().getPreyInitialPopulation();
        final int initPredator = Configuration.getConfiguration().getPredatorInitialPopulation();

        for (int i = 0; i < initPrey + initPredator; i++) {
            final double speed = Rng.RNG.nextDouble(minSpeed, maxSpeed);
            final Point spawnPoint = new Point(
                Rng.RNG.nextDouble(0, this.mapSizeX * this.gridSize),
                Rng.RNG.nextDouble(0, this.mapSizeY * this.gridSize)
            );

            final double angle = Rng.RNG.nextDouble(0, 2 * Math.PI);

            final Entity entity = i < initPrey
                ? new Prey(this, speed, spawnPoint, angle)
                : new Predator(this, speed, spawnPoint, angle);

            this.addEntity(entity);
        }
    }

    /**
     * Adds an entity to the list of entities. Automatically adds it to the correct grid.
     * @param entity The entity to be added
     */
    public void addEntity(final Entity entity) {
        entity.setSimulation(this);
        if (entity instanceof Predator) {
            ++this.predatorCount;
        } else if (entity instanceof Prey) {
            ++this.preyCount;
        } else {
            throw new IllegalArgumentException("Unrecognized Entity: " + entity);
        }

        final Coordinate coord = pointToGridCoord(entity.getBodyCenter());

        final ReadWriteLockedItem<List<Entity>> chunk =
            this.updateToAdd[coord.getX()][coord.getY()];

        chunk.writeLock().lock();
        try {
            chunk.get().add(entity);
        } finally {
            chunk.writeLock().unlock();
        }
    }

    private void checkCollisions(final Entity a, final Entity b) throws EvosimException {
        if (a.collidesWith(b)) {
            a.onCollide(b);
            b.onCollide(a);
        }
    }


    private void update() {
        IntStream.range(0, this.mapSizeX * this.mapSizeY).forEach(
            (chunkIndex) -> {
                final Coordinate chunkCoord = new Coordinate(
                    chunkIndex % this.mapSizeX,
                    chunkIndex / this.mapSizeX
                );

                final ReadWriteLockedItem<List<Entity>> chunk =
                    this.entityGrids[chunkCoord.getX()][chunkCoord.getY()];

                chunk.readLock().lock();
                try {
                    final int entityCount = chunk.get().size();
                    IntStream.range(0, entityCount).parallel().forEach((i) -> {
                        final Entity entity = chunk.get().get(i);

                        final Coordinate oldCoord = pointToGridCoord(entity.getBodyCenter());

                        try {
                            entity.update();
                        } catch (EvosimException e) {
                            throw new RuntimeException(e);
                        }

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
                            try {
                                if (
                                    (
                                        entity instanceof Prey
                                            && this.preyCount
                                            < Configuration.getConfiguration().getPreyMaxNumber()
                                    )
                                        || (
                                        entity instanceof Predator
                                            && this.predatorCount
                                            < Configuration.getConfiguration()
                                                .getPredatorMaxNumber()
                                    )
                                ) {
                                    final ReadWriteLockedItem<List<Entity>> chk =
                                        this.updateToAdd[oldCoord.getX()][oldCoord.getY()];

                                    chk.writeLock().lock();
                                    try {
                                        chk.get().add(entity.clone());
                                    } finally {
                                        chk.writeLock().unlock();
                                    }

                                    entity.setSplitEnergy(0);
                                    entity.setChildCount(entity.getChildCount() + 1);
                                    entity.setSplit(false);

                                    if (entity instanceof Prey) {
                                        ++this.preyCount;
                                    } else {
                                        ++this.predatorCount;
                                    }
                                }
                            } catch (EvosimException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        final Coordinate curCoord = pointToGridCoord(entity.getBodyCenter());

                        if (!curCoord.equals(oldCoord)) {
                            final ReadWriteLockedItem<List<Entity>> chkFrom =
                                this.updateToRemove[oldCoord.getX()][oldCoord.getY()];

                            final ReadWriteLockedItem<List<Entity>> chkTo =
                                this.updateToAdd[curCoord.getX()][curCoord.getY()];

                            chkFrom.writeLock().lock();
                            try {
                                chkFrom.get().add(entity);
                            } finally {
                                chkFrom.writeLock().unlock();
                            }

                            chkTo.writeLock().lock();
                            try {
                                chkTo.get().add(entity);
                            } finally {
                                chkTo.writeLock().unlock();
                            }
                        }

                        this.collisionCheckerService.execute(this.submitCollisionWork(entity));
                    });
                } finally {
                    chunk.readLock().unlock();
                }
            }
        );

        for (int i = 0; i < this.mapSizeX; ++i) {
            for (int j = 0; j < this.mapSizeY; ++j) {
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
    public boolean isPointValid(final Point point) {
        return (
            0 <= point.getX() && point.getX() < this.mapSizeX * this.gridSize
            && 0 <= point.getY() && point.getY() < this.mapSizeY * this.gridSize
        );
    }

    /**
     * Verifies if a given grid coordinate is valid.
     * @param x The x component of the coordinate to validate
     * @param y The y component of the coordinate to validate
     * @return Whether it is valid
     */
    public boolean isCoordValid(final int x, final int y) {
        return 0 <= x && x < this.mapSizeX && 0 <= y && y < this.mapSizeY;
    }

    /**
     * Verifies if a given grid coordinate is valid.
     * @param coord The coordinate to validate
     * @return Whether it is valid
     */
    public boolean isCoordValid(final Coordinate coord) {
        return isCoordValid(coord.getX(), coord.getY());
    }

    /**
     * Converts a point to the grid coordinate it is in.
     *
     * @param point THe point to be converted.
     * @return The coordinate of the grid containing the point.
     */
    public Coordinate pointToGridCoord(final Point point) {
        return new Coordinate(
            Math.min((int) (point.getX() / this.gridSize), this.mapSizeX - 1),
            Math.min((int) (point.getY() / this.gridSize), this.mapSizeY - 1)
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
            this::update, 0, UPDATE_INTERVAL_NANO, TimeUnit.NANOSECONDS
        );
    }

    /**
     * Pauses updating the simulation.
     */
    public void pauseUpdate() {
        if (this.currentUpdate != null) {
            this.currentUpdate.cancel(true);
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

    private Runnable submitCollisionWork(final Entity entity) {
        return () -> {
            final Coordinate center = this.pointToGridCoord(entity.getBodyCenter());

            for (int x = center.getX() - 1; x <= center.getX() + 1; ++x) {
                for (int y = center.getY() - 1; y <= center.getY() + 1; ++y) {
                    if (!isCoordValid(x, y)) {
                        continue;
                    }

                    this.forEachGridEntities(x, y, other -> {
                        try {
                            this.checkCollisions(entity, other);
                        } catch (EvosimException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        };
    }
}
