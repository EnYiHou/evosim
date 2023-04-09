package org.totallyspies.evosim.math;

/**
 * This class contains all the assertion methods.
 */
public final class Assert {
    private Assert() {
    }

    /**
     * Asserts that a given double is equal to the expected value within a given delta.
     * @param expected the expected value
     * @param actual the actual value
     * @param delta the delta
     * @return true if the actual value is equal to the expected
     * value within the delta, false otherwise.
     */
    public static boolean assertEquals(final double actual,
                                       final double expected,
                                       final double delta) {
        if (Math.abs(expected - actual) > delta) {
            return false;
        }
        return true;
    }

}
