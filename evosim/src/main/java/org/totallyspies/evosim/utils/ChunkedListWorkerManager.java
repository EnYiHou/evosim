package org.totallyspies.evosim.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Class providing a way to iterate over a list split into chunks forever in a multithreaded way.
 * @param <T> the type of elements in this collection
 *
 * @author ptrstr
 */
public final class ChunkedListWorkerManager<T> {

  /**
   * Group to which all threads belong. Allows simple closing and managing of the threads.
   */
  private final ThreadGroup workerGroup;

  /**
   * Threads that will iterate over the chunks forever.
   */
  private final Thread[] workers;

  /**
   * Fixed-size array of lists which are the chunks.
   */
  private final List<T>[] chunks;

  /**
   * Initializes and starts {@code workerCount} workers to iterate over {@code chunkCount} chunks
   * mapping them to the given {@code task}.
   * @param chunkCount Number of chunks to create.
   * @param workerCount Number of workers to create.
   * @param task Task to run on each chunk. BiConsumer of index of the chunk and the chunk itself.
   */
  public ChunkedListWorkerManager(
      final int chunkCount,
      final int workerCount,
      final BiConsumer<Integer, List<T>> task
  ) {
    if (workerCount < 1) {
      throw new IllegalArgumentException("Must have at least 1 worker");
    }

    this.workerGroup = new ThreadGroup("workers");
    this.workers = new Thread[workerCount];
    this.chunks = (List<T>[]) (new List[chunkCount]);
    for (int i = 0; i < chunkCount; ++i) {
      this.chunks[i] = Collections.synchronizedList(new LinkedList<>());
    }

    final int chunksPerWorker = chunkCount / workerCount;
    for (int i = 0; i < workerCount; ++i) {
      this.workers[i] = new Thread(
          this.workerGroup,
          this.generateWorkerFunction(
              i * chunksPerWorker,
              (i + 1) * (chunksPerWorker + (i == 0 ? chunkCount % workerCount : 0)),
              task
          ),
          String.format("Worker-%d", i)
      );

      this.workers[i].start();
    }
  }

  private Runnable generateWorkerFunction(
      final int firstChunk,
      final int lastChunk,
      final BiConsumer<Integer, List<T>> task
  ) {
    return () -> {
      while (!Thread.currentThread().isInterrupted()) {
        for (int i = firstChunk; i < lastChunk; ++i) {
          task.accept(i, this.chunks[i]);
        }
      }
    };
  }

  /**
   * Removes the first instance of {@code target} found in all chunks.
   * @param target Instance to be removed.
   * @return If the instance was removed.
   */
  public boolean remove(final T target) {
    boolean removed = false;

    for (int i = 0; !removed && i < this.chunks.length; ++i) {
      removed = this.chunks[i].remove(target);
    }

    return removed;
  }

  /**
   * Removes the first instance of {@code target} found in given chunk.
   * @param target Instance to be removed.
   * @param chunkIndex Index of the chunk to be looked into.
   * @return If the instance was removed.
   */
  public boolean remove(final T target, final int chunkIndex) {
    return this.chunks[chunkIndex].remove(target);
  }

  /**
   * Adds an instance to a given chunk.
   * @param target Instance to be added.
   * @param chunkIndex Index of the chunk to be modified.
   * @return If the instance was added.
   */
  public boolean add(final T target, final int chunkIndex) {
    return this.chunks[chunkIndex].add(target);
  }

  /**
   * Adds every instance from a list to a given chunk.
   * @param target List of instances to be added.
   * @param chunkIndex Index of the chunk to be modified.
   * @return If all instances were added.
   */
  public boolean addAll(final List<T> target, final int chunkIndex) {
    return this.chunks[chunkIndex].addAll(target);
  }

  /**
   * Moves instance from one chunk to another.
   * @param target Instance to be moved.
   * @param from Index of chunk where instance is.
   * @param to Index of chunk where instance should go.
   * @return If the instance was moved.
   */
  public boolean move(final T target, final int from, final int to) {
    return this.remove(target, from) && this.add(target, to);
  }

  /**
   * Gets chunk at given index.
   * @param i Index of chunk.
   * @return Chunk at index.
   */
  public List<T> getChunk(final int i) {
    return this.chunks[i];
  }

  public int getChunkCount() {
    return this.chunks.length;
  }

  /**
   * Interrupts and stops all workers.
   */
  public void interrupt() {
    this.workerGroup.interrupt();
  }
}
