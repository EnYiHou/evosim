package org.totallyspies.evosim.utils;

import java.util.Random;

/**
 * A utility class with a final Random object used for generating random values
 * with the same seed.
 *
 * @author Matthew
 */
public class Rng {

    private Rng() {
    }

    /**
     * A <code>Random</code> used for generating randomly generated values.
     */
    public static final Random RNG = new Random();

}
