package org.totallyspies.evosim.simulation;

import java.util.Date;
import java.util.LinkedList;

import javafx.animation.AnimationTimer;
import lombok.Getter;
import lombok.Setter;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.entities.Prey;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.ChunkedListWorkerManager;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.Rng;

import java.util.ArrayList;
import java.util.List;

/**
 * The class in which the bulk of the simulation loop is managed.
 *
 * @author Matthew
 */
public final class Simulation {
    /**
     * The width of the whole map.
     */
    public static final int MAP_SIZE_X = 10;
    /**
     * The height of the whole map.
     */
    public static final int MAP_SIZE_Y = 10;
    /**
     * The x and y size of a single grid.
     */
    public static final int GRID_SIZE = 500;
    private static List<Simulation> simulations = new LinkedList<>();
    /**
     * Grids of entities.
     */
    private final ChunkedListWorkerManager<Entity> entityGrids;

    /**
     * The animation loop of the simulation that runs every frame.
     */
    @Getter
    private final AnimationTimer animationLoop;

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
     * Constructs a new simulation based on the default configuration.
     */
    public Simulation() {
        this.entityGrids = new ChunkedListWorkerManager<>(
                MAP_SIZE_X * MAP_SIZE_Y,
                100,
                this::checkGridCollisions
        );

        // TODO add initial population (add config for initial population)
        this.populateEntityList(100, 100);

        this.animationLoop = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                update(now);
            }
        };

        this.animationLoop.start();
        this.entityGrids.startWorkers();

        simulations.add(this);
    }

    public static void stopAll() {
        simulations.forEach(simulation -> simulation.entityGrids.stopWorkers());
    }

    /**
     * Converts a {@code Point} to a chunk index for {@link #entityGrids}.
     *
     * @param point Point to be converted.
     * @return Index of chunk for this point.
     */
    public static int pointToChunk(final Point point) {
        return coordsToChunk((int) point.getX() / GRID_SIZE, (int) point.getY() / GRID_SIZE);
    }

    /**
     * Converts a coordinate to a chunk index for {@link #entityGrids}.
     *
     * @param x X axis index.
     * @param y Y axis index.
     * @return Index of chunk for this point.
     */
    public static int coordsToChunk(final int x, final int y) {
        return x + y * MAP_SIZE_X;
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
        final List<Entity> entities = new ArrayList<>(initPrey + initPredator);

        // TODO add min speed to config
        for (int i = 0; i < initPrey; i++) {
            entities.add(new Prey(
                    Rng.RNG.nextDouble(1, maxSpeed),
                    new Point(
                            Rng.RNG.nextDouble(0, MAP_SIZE_X * GRID_SIZE),
                            Rng.RNG.nextDouble(0, MAP_SIZE_Y * GRID_SIZE)
                    ), Rng.RNG.nextDouble(0, 2 * Math.PI), System.currentTimeMillis()));
        }

        for (int i = 0; i < initPredator; i++) {
            entities.add(new Predator(
                    Rng.RNG.nextDouble(1, maxSpeed),
                    new Point(
                            Rng.RNG.nextDouble(0, MAP_SIZE_X * GRID_SIZE),
                            Rng.RNG.nextDouble(0, MAP_SIZE_Y * GRID_SIZE)
                    ), Rng.RNG.nextDouble(0, 2 * Math.PI), System.currentTimeMillis()));
        }

        entities.forEach(
                entity -> this.entityGrids.add(entity, pointToChunk(entity.getBodyCenter()))
        );

        this.preyCount += initPrey;
        this.predatorCount += initPredator;
    }

    private void checkGridCollisions(final int i, final List<Entity> entities) {
        List<Entity> chunk;
        synchronized (entities) {
            for (Entity cur : entities) {
                for (int x = i - 1; x < i + 1; ++x) {
                    if (x < 0 || MAP_SIZE_X <= x) {
                        continue;
                    }

                    for (int y = i - 1; y < i + 1; ++y) {
                        if (y < 0 || MAP_SIZE_Y <= y) {
                            continue;
                        }

                        chunk = this.entityGrids.getChunk(coordsToChunk(x, y));
                        synchronized (chunk) {
                            for (Entity toCompare : chunk) {
                                if (cur.collidesWith(toCompare)) {
                                    cur.onCollide(toCompare);
                                    toCompare.onCollide(cur);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void update(final long now) {
        for (int i = 0; i < this.entityGrids.getChunkCount(); ++i) {
            List<Entity> chunk = this.entityGrids.getChunk(i);
            synchronized (chunk) {
                for (int j = chunk.size() - 1; j >= 0; --j) {
                    final Entity entity = chunk.get(j);
                    entity.update();
                    if (entity.isDead()) {
                        chunk.remove(j);
                        if (entity instanceof Prey) {
                            this.preyCount--;
                        } else if (entity instanceof Predator) {
                            this.predatorCount--;
                        }
                    } else if (entity.isSplit()) {
                        chunk.add(entity.clone());
                        entity.setSplitEnergy(0);
                        entity.setChildCount(entity.getChildCount() + 1);
                        entity.setSplit(false);
                        if (entity instanceof Prey) {
                            this.preyCount++;
                        } else if (entity instanceof Predator) {
                            this.predatorCount++;
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets a copy of all entiries within a grid at this instant.
     *
     * @param x X position of grid
     * @param y Y position of grid
     * @return Frozen list of entities within the grid.
     */
    public List<Entity> getGridEntities(final int x, final int y) {
        List<Entity> entities;
        List<Entity> chunk = this.entityGrids.getChunk(coordsToChunk(x, y));

        synchronized (chunk) {
            entities = chunk.stream().toList();
        }

        return entities;
    }
}
