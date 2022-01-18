package com.xb.curator.common.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Future task scheduler.
 *
 * @ClassName FutureTaskScheduler
 * @Description TODO
 * @Author xb
 * @Date 2021 /11/28 20:45
 * @Version 1.0
 */
public class FutureTaskScheduler extends Thread {
  private ConcurrentLinkedQueue<ExecuteTask> executeTasksQueue = new ConcurrentLinkedQueue<>();

  private static FutureTaskScheduler instance = new FutureTaskScheduler();

  private ExecutorService pool = Executors.newFixedThreadPool(10);

  private FutureTaskScheduler() {
    this.start();
  }


  /**
   * 添加任务
   *
   * @param task
   *     the task
   */
  public static void add(ExecuteTask task) {
    instance.executeTasksQueue.add(task);
  }

  @Override
  public void run() {
    super.run();
  }

  private void handleTask() {
    ExecuteTask executeTask;
    while (executeTasksQueue.peek() != null) {
      executeTask = executeTasksQueue.poll();
      handleTask(executeTask);
    }
  }

  /**
   * 执行任务操作
   */
  private void handleTask(ExecuteTask executeTask) {
    pool.execute(new ExecuteRunnable(executeTask));
  }

  class ExecuteRunnable implements Runnable {
    ExecuteTask executeTask;

    ExecuteRunnable(ExecuteTask executeTask) {
      this.executeTask = executeTask;
    }

    @Override
    public void run() {
      executeTask.execute();
    }
  }
}
