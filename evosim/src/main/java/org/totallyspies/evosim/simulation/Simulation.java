package org.totallyspies.evosim.simulation;

import javafx.animation.AnimationTimer;
import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.entities.Prey;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.ui.Map;
import org.totallyspies.evosim.utils.Configuration;
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
     * The singleton instance of the simulation.
     */
    private static final Simulation INSTANCE = new Simulation();

    /**
     * The animation loop of the simulation that runs every frame.
     */
    private final AnimationTimer animationLoop = new AnimationTimer() {
        @Override
        public void handle(final long now) {
            updateGrids();

            ListIterator<Entity> iterator = Simulation.ENTITY_LIST.listIterator();
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
        this.generateGrids();
        // TODO add initial population (add config for initial population)
        this.populateEntityList(100, 100);
        this.updateGrids();
    }

    /**
     * Generate the grids for the simulation.
     */
    private void generateGrids() {

        for (int i = 0; i < Map.ROW_COLUMN_COUNT; i++) {
            final List<List<Entity>> grid = new ArrayList<>();
            Simulation.GRIDS.add(grid);
            for (int j = 0; j < Map.ROW_COLUMN_COUNT; j++) {
                grid.add(new ArrayList<>());
            }
        }
    }

    /**
     * Populates the entity list by constructing all initial entities based on user given initial
     * populations.
     *
     * @param initPrey      the initial number of prey spawned
     * @param initPredator  the initial number of predators spawned
     */
    private void populateEntityList(final int initPrey, final int initPredator) {
        double maxSpeed = Configuration.getCONFIGURATION().getEntityMaxSpeed();

        // TODO add min speed to config
        for (int i = 0; i < initPrey; i++) {
            Simulation.ENTITY_LIST.add(new Prey(
                    Rng.RNG.nextDouble(1, maxSpeed),
                    new Point(
                            Rng.RNG.nextDouble(0, Map.MAP_SIZE),
                            Rng.RNG.nextDouble(0, Map.MAP_SIZE)
                    ),
                    Rng.RNG.nextDouble(0, 2 * Math.PI)
            ));
        }

        for (int i = 0; i < initPredator; i++) {
            Simulation.ENTITY_LIST.add(new Predator(
                    Rng.RNG.nextDouble(1, maxSpeed),
                    new Point(
                            Rng.RNG.nextDouble(0, Map.MAP_SIZE),
                            Rng.RNG.nextDouble(0, Map.MAP_SIZE)
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
        for (List<List<Entity>> grid : Simulation.GRIDS) {
            for (List<Entity> entities : grid) {
                entities.clear();
            }
        }

        for (final Entity entity : Simulation.ENTITY_LIST) {
            final int x = (int) (entity.getBodyCenter().getX() / Map.GRID_SIZE);
            final int y = x;
            Simulation.GRIDS.get(x).get(y).add(entity);
            entity.setGridX(x);
            entity.setGridY(y);
        }
    }
}
