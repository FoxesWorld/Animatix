package org.foxesworld.animatix.animation;

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
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        logger.log(System.Logger.Level.INFO, "TaskExecutor initialized with thread pool size: {0}", threadPoolSize);
    }

    /**
     * Выполняет список задач параллельно с ожиданием завершения.
     *
     * @param tasks   Список задач для выполнения.
     * @param timeout Тайм-аут в миллисекундах.
     * @param onError Обработчик ошибок выполнения задач.
     * @throws InterruptedException Если выполнение прервано.
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
                    logger.log(System.Logger.Level.WARNING, "Task execution timed out");
                }
            } catch (TimeoutException e) {
                future.cancel(true);
                logger.log(System.Logger.Level.WARNING, "Task execution timed out", e);
                onError.accept(e);
            } catch (Exception e) {
                logger.log(System.Logger.Level.ERROR, "Task execution failed", e);
                onError.accept(e);
            }
        }
    }

    /**
     * Выполняет одиночную задачу с таймаутом.
     *
     * @param task    Задача для выполнения.
     * @param timeout Тайм-аут в миллисекундах.
     * @param <T>     Тип возвращаемого значения.
     * @return Результат выполнения задачи.
     * @throws TimeoutException      Если задача не завершилась вовремя.
     * @throws InterruptedException  Если выполнение прервано.
     * @throws ExecutionException    Если задача завершилась с ошибкой.
     */
    public <T> T executeTaskWithTimeout(Callable<T> task, long timeout) throws TimeoutException, InterruptedException, ExecutionException {
        Future<T> future = executorService.submit(task);
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            logger.log(System.Logger.Level.WARNING, "Task execution timed out after {0} ms", timeout);
            throw e;
        }
    }

    /**
     * Запускает задачу асинхронно.
     *
     * @param task Задача для выполнения.
     * @param onError Обработчик ошибок выполнения задачи.
     */
    public void submitTask(Runnable task, Consumer<Exception> onError) {
        executorService.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.log(System.Logger.Level.ERROR, "Async task execution failed", e);
                onError.accept(e);
            }
        });
    }

    /**
     * Завершает работу TaskExecutor и освобождает ресурсы.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                List<Runnable> cancelledTasks = executorService.shutdownNow();
                logger.log(System.Logger.Level.WARNING, "Executor forcefully shutdown. Cancelled tasks: {0}", cancelledTasks.size());
            } else {
                logger.log(System.Logger.Level.INFO, "Executor shut down gracefully");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            logger.log(System.Logger.Level.ERROR, "Shutdown interrupted", e);
        }
    }
}
