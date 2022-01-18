package com.xb.curator;

import com.xb.curator.common.concurrent.FutureTaskScheduler;
import com.xb.curator.distributedLock.ZKLock;
import org.junit.jupiter.api.Test;

/**
 * @ClassName ZkLockTester
 * @Description TODO
 * @Author xb
 * @Date 2021/11/28 20:39
 * @Version 1.0
 **/
public class ZkLockTester {
  int count = 0;

  @Test
  public void testLock() throws InterruptedException {
    for (int i = 0; i < 10; i++) {
      FutureTaskScheduler.add(() -> {
        ZKLock zkLock = new ZKLock();
        zkLock.lock();

        for (int j = 0; j < 10; j++) {
          count++;
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("count:" + count);
        zkLock.unLock();
      });
    }
  }
}
