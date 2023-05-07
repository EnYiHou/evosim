package org.totallyspies.evosim.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Read and write lock wrapped around a value.
 * @param <T> The type to be held.
*  @author ptrstr
 */
public class ReadWriteLockedItem<T> {

  /**
   * Lock used to protect the item.
   */
  private final ReentrantReadWriteLock lock;

  /**
   * The item to be protected by the lock.
   */
  private volatile T item;

  /**
   * Creates a locked item with a {@code null} value.
   */
  public ReadWriteLockedItem() {
    this(null);
  }

  /**
   * Creates a locked item with an initial value.
   * @param newItem The initial value of the held item.
   */
  public ReadWriteLockedItem(final T newItem) {
    this.lock = new ReentrantReadWriteLock(true);
    this.item = newItem;
  }

  /**
   * Gets the read lock only.
   * @return The read lock
   */
  public Lock readLock() {
    return this.lock.readLock();
  }

  /**
   * Gets the write lock only.
   * @return The write lock
   */
  public Lock writeLock() {
    return this.lock.writeLock();
  }

  /**
   * Sets the held value. This must be called when either the {@link #writeLock} or
   * {@link #readLock} is held.
   * @return The held value.
   */
  public T get() {
    return this.item;
  }

  /**
   * Sets the held value. This must be called when the {@link #writeLock} is held.
   * @param newItem The held value.
   */
  public void set(final T newItem) {
    this.item = newItem;
  }
}
