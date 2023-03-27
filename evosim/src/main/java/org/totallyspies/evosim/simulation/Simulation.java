package org.totallyspies.evosim.simulation;

import javafx.animation.AnimationTimer;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.entities.Prey;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.Rng;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * The class in which the bulk of the simulation loop is managed.
 *
 * @author Matthew
 */
public final class Simulation {
    /**
     * A list containing a list of all living entities.
     */
    public static final List<Entity> ENTITY_LIST = new ArrayList<>();

    /**
     * The list of grids that the entities are stored in.
     */
    public static final List<List<List<Entity>>> GRIDS = new ArrayList<>();

    /**
     * The height of a single grid.
     */
    public static final double GRID_HEIGHT = 500.0d;

    /**
     * The width of a single grid.
     */
    public static final double GRID_WIDTH = 500.0d;

    /**
     * The height of the whole map.
     */
    public static final double MAP_HEIGHT = 5000.0d;

    /**
     * The width of the whole map.
     */
    public static final double MAP_WIDTH = 5000.0d;

    /**
     * The number of grids in the x direction.
     */
    public static final int GRID_X = (int) (MAP_WIDTH / GRID_WIDTH);

    /**
     * The number of grids in the y direction.
     */
    public static final int GRID_Y = (int) (MAP_HEIGHT / GRID_HEIGHT);

    /**
     * The animation loop of the simulation that runs every frame.
     */
    private final AnimationTimer animationLoop = new AnimationTimer() {
        @Override
        public void handle(final long now) {

            updateGrids();

            ListIterator<Entity> iterator = ENTITY_LIST.listIterator();
            while (iterator.hasNext()) {
                final Entity entity = iterator.next();
                entity.update();
                if (entity.getDeath()) {
                    iterator.remove();
                }
                if (entity.getSplit()) {
                    iterator.add(entity.clone());
                    entity.setSplitEnergy(entity.getSplitEnergy() - 1);
                    entity.setChildCount(entity.getChildCount() + 1);
                    entity.setSplit(false);
                }
            }
        }
    };

    private Simulation() {
    }

    /**
     * Generate the grids for the simulation.
     */
    public void generateGrids() {
        final int x = (int) (MAP_WIDTH / GRID_WIDTH);
        final int y = (int) (MAP_HEIGHT / GRID_HEIGHT);

        for (int i = 0; i < x; i++) {
            final List<List<Entity>> grid = new ArrayList<>();
            for (int j = 0; j < y; j++) {
                grid.add(new ArrayList<>());
            }
            GRIDS.add(grid);
        }
    }

    /**
     * Populates the entity list by constructing all initial entities based
     * on user given initial populations.
     *
     * @param initPrey      the initial number of prey spawned
     * @param initPredator  the initial number of predators spawned
     */
    public void populateEntityList(final int initPrey, final int initPredator) {
        for (int i = 0; i < initPrey; i++) {
            ENTITY_LIST.add(new Prey(
                    Rng.RNG.nextDouble(Entity.MIN_SPEED, Entity.MAX_SPEED),
                    new Point(
                            Rng.RNG.nextDouble(0, Simulation.MAP_WIDTH),
                            Rng.RNG.nextDouble(0, Simulation.MAP_HEIGHT)
                    ),
                    Rng.RNG.nextDouble(0, 2 * Math.PI)
            ));
        }

        for (int i = 0; i < initPredator; i++) {
            ENTITY_LIST.add(new Predator(
                    Rng.RNG.nextDouble(Entity.MIN_SPEED, Entity.MAX_SPEED),
                    new Point(
                            Rng.RNG.nextDouble(0, Simulation.MAP_WIDTH),
                            Rng.RNG.nextDouble(0, Simulation.MAP_HEIGHT)
                    ),
                    Rng.RNG.nextDouble(0, 2 * Math.PI)
            ));
        }
    }

    /**
     * Updates the grid map by clearing all entities from the grid map and
     * re-adding them based on their current position.
     */
    private void updateGrids() {
        //clear entity from grids
        for (int i = 0; i < GRIDS.size(); i++) {
            for (int j = 0; j < GRIDS.get(i).size(); j++) {
                GRIDS.get(i).get(j).clear();
            }
        }

        for (int i = 0; i < ENTITY_LIST.size(); i++) {
            final Entity entity = ENTITY_LIST.get(i);
            final int x = (int) (entity.getBodyCenter().getX()
                    / Simulation.GRID_WIDTH);
            final int y = (int) (entity.getBodyCenter().getY()
                    / Simulation.GRID_WIDTH);
            GRIDS.get(x).get(y).add(entity);
            entity.setGridX(x);
            entity.setGridY(y);
        }
    }
}
