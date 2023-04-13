package org.totallyspies.evosim.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Random;

/**
 * A utility class with a final Random object used for generating random values with the same seed.
 *
 * @author Matthew
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class Rng {
    /**
     * A <code>Random</code> used for generating randomly generated values.
     */
    public static final Random RNG = new Random();

}
