package org.totallyspies.evosim.simulation;

import org.totallyspies.evosim.entities.Entity;
import org.totallyspies.evosim.entities.Predator;
import org.totallyspies.evosim.entities.Prey;
import org.totallyspies.evosim.geometry.Point;
import org.totallyspies.evosim.utils.Rng;

import java.util.ArrayList;
import java.util.List;

/**
 * The class in which the bulk of the simulation loop is managed.
 *
 * @author Matthew
 */
public final class SimulationApp {
    /**
     * A list containing a list of all living entities.
     */
    public static final List<Entity> ENTITY_LIST = new ArrayList<>();

    /**
     * The maximum height of the grid-map.
     */
    public static final double GRID_MAX_HEIGHT = 5000.0d;

    /**
     * The maximum width of the grid-map.
     */
    public static final double GRID_MAX_WIDTH = 5000.0d;

    /**
     * The height of the complete grid-map.
     */
    private double gridHeight;

    /**
     * The width of the complete grid-map.
     */
    private double gridWidth;

    private SimulationApp() { };

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
                            Rng.RNG.nextDouble(0, gridWidth),
                            Rng.RNG.nextDouble(0, gridHeight)
                    ),
                    Math.toRadians(Rng.RNG.nextDouble(0, 360))
            ));
        }

        for (int i = 0; i < initPredator; i++) {
            ENTITY_LIST.add(new Predator(
                    Rng.RNG.nextDouble(Entity.MIN_SPEED, Entity.MAX_SPEED),
                    new Point(
                            Rng.RNG.nextDouble(0, gridWidth),
                            Rng.RNG.nextDouble(0, gridHeight)
                    ),
                    Math.toRadians(Rng.RNG.nextDouble(0, 360))
            ));
        }
    }

}
