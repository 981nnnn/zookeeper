package com.xb.curator.distributedLock;

/**
 * @ClassName Lock
 * @Description TODO
 * @Author xb
 * @Date 2021/11/27 14:34
 * @Version 1.0
 **/
public interface Lock {
  /**
   * 是否加锁成功
   * @return
   */
  boolean lock();

  /**
   * 是否解锁成功
   * @return
   */
  boolean unLock();
}
