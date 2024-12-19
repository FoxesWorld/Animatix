package org.foxesworld.animatix.animation.task;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class TaskExecutor {
    private static final System.Logger logger = System.getLogger(TaskExecutor.class.getName());
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executorService;

    public TaskExecutor() {
        this(THREAD_POOL_SIZE);
    }

    public TaskExecutor(int threadPoolSize) {
        this.executorService = Executors.newWorkStealingPool(threadPoolSize);
        logger.log(System.Logger.Level.INFO, "TaskExecutor initialized with thread pool size: {0}", threadPoolSize);
    }

    /**
     * Executes a collection of tasks in parallel and waits for completion with a timeout.
     *
     * @param tasks   List of tasks to execute.
     * @param timeout Timeout in milliseconds.
     * @param onError Error handler for task failures.
     * @throws InterruptedException If execution is interrupted.
     */
    public void executeTasksWithTimeout(Collection<? extends Callable<?>> tasks, long timeout, Consumer<Exception> onError) throws InterruptedException {
        List<Future<?>> futures = new ArrayList<>();
        for (Callable<?> task : tasks) {
            futures.add(executorService.submit(task));
        }

        long endTime = System.currentTimeMillis() + timeout;
        for (Future<?> future : futures) {
            try {
                long remainingTime = endTime - System.currentTimeMillis();
                if (remainingTime > 0) {
                    future.get(remainingTime, TimeUnit.MILLISECONDS);
                } else {
                    future.cancel(true);
                    logger.log(System.Logger.Level.WARNING, "Task execution timed out and was cancelled.");
                }
            } catch (TimeoutException e) {
                future.cancel(true);
                logger.log(System.Logger.Level.WARNING, "Task execution timed out.", e);
                onError.accept(e);
            } catch (Exception e) {
                logger.log(System.Logger.Level.ERROR, "Task execution failed.", e);
                onError.accept(e);
            }
        }
    }

    /**
     * Executes a single task with a timeout.
     *
     * @param task    The task to execute.
     * @param timeout Timeout in milliseconds.
     * @param <T>     Return type.
     * @return Task result.
     * @throws TimeoutException If task does not complete in time.
     * @throws InterruptedException If execution is interrupted.
     * @throws ExecutionException If task fails.
     */
    public <T> T executeTaskWithTimeout(Callable<T> task, long timeout) throws TimeoutException, InterruptedException, ExecutionException {
        Future<T> future = executorService.submit(task);
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            logger.log(System.Logger.Level.WARNING, "Task execution timed out after {0} ms.", timeout);
            throw e;
        }
    }

    /**
     * Submits a task for asynchronous execution.
     *
     * @param task    The task to execute.
     * @param onError Error handler for task failures.
     */
    public void submitTask(Runnable task, Consumer<Exception> onError) {
        executorService.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.log(System.Logger.Level.ERROR, "Async task execution failed.", e);
                onError.accept(e);
            }
        });
    }

    /**
     * Shuts down the TaskExecutor and releases resources.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                List<Runnable> cancelledTasks = executorService.shutdownNow();
                logger.log(System.Logger.Level.WARNING, "Executor forcefully shutdown. Cancelled tasks: {0}", cancelledTasks.size());
            } else {
                logger.log(System.Logger.Level.INFO, "Executor shut down gracefully.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            logger.log(System.Logger.Level.ERROR, "Shutdown interrupted.", e);
        }
    }
}
