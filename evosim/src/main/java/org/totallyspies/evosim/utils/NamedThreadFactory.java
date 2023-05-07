package org.totallyspies.evosim.utils;

import java.util.concurrent.ThreadFactory;
import lombok.Data;

/**
 * Thread factory that names thread with a custom base name.
 * @author ptrstr
 */
@Data
public class NamedThreadFactory implements ThreadFactory {

  /**
   * Base name to use for the thread factory.
   */
  private final String baseName;

  /**
   * Current index of created threads.
   */
  private int i = 0;

  /**
   * Creates a new thread with the base name.
   * @param r Function to run in the thread
   * @return The named thread
   */
  public final Thread newThread(final Runnable r) {
    return new Thread(r, String.format("%s-%d", baseName, i++));
  }
}
