package com.xb.curator.distributedLock;

import com.xb.curator.config.ZKClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Zk lock.
 *
 * @ClassName ZKLock
 * @Description TODO
 * @Author xb
 * @Date 2021 /11/27 14:34
 * @Version 1.0
 */

public class ZKLock implements Lock {
  private static Logger logger = LoggerFactory.getLogger(ZKLock.class);
  //ZkLock的节点链接
  private static final String ZK_PATH = "/test/lock";
  private static final String LOCK_PREFIX = ZK_PATH + "/";
  private static final long WAIT_TIME = 1000;
  /**
   * The Lock count.
   */
  final AtomicInteger lockCount = new AtomicInteger(0);
  /**
   * The Client.
   */
//Zk客户端
  CuratorFramework client = null;
  private String locked_short_path = null;
  private String locked_path = null;
  private String prior_path = null;
  private Thread thread;


  /**
   * Instantiates a new Zk lock.
   *
   * @param config
   *     the config
   * @param lockName
   *     the lock name
   */
  public ZKLock() {
    ZKClient.instance.init();
    if (!ZKClient.instance.isNodeExist(ZK_PATH)) {
      ZKClient.instance.createNode(ZK_PATH, null);
    }
    client = ZKClient.instance.getClient();
  }

  @Override
  public boolean lock() {
    synchronized (this) {
      if (lockCount.get() == 0) {
        thread = Thread.currentThread();
        lockCount.incrementAndGet();
      } else {
        if (!thread.equals(Thread.currentThread())) {
          return false;
        }
        lockCount.incrementAndGet();
        return true;
      }
    }
    try {
      boolean locked = false;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean unLock() {
    if (!thread.equals(Thread.currentThread())) {
      return false;
    }
    int newLockCount = lockCount.decrementAndGet();
    if (newLockCount < 0) {
      throw new IllformedLocaleException("Lock count has gone negative for lock: " + locked_path);
    }
    if (newLockCount != 0) {
      return true;
    }
    try {
      if (ZKClient.instance.isNodeExist(locked_path)) {
        client.delete().forPath(locked_path);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private boolean tryLock() throws Exception {
    List<String> waiters = getWaiters();
    locked_path = ZKClient.instance.createEphemeralSeqNode(LOCK_PREFIX, CreateMode.PERSISTENT);
    if (locked_path == null) {
      throw new Exception();
    }
    locked_short_path = getShortPath(locked_path);

    // 获取等待的子节点列表，判断自己是否是第一个
    if (checkLocked(waiters)) {
      return true;
    }
    int index = Collections.binarySearch(waiters, locked_short_path);
    if (index < 0) {
      // 网络抖动，获取到的子节点列表里可能已经没有自己了
      throw new Exception("节点没有找到: " + locked_short_path);
    }
    // 如果自己没有获得锁
    prior_path = ZK_PATH + "/" + waiters.get(index - 1);
    return false;
  }


  /**
   * 获取最短路径
   *
   * @param locked_path
   *     路径地址
   * @return 最短路径
   */
  private String getShortPath(String locked_path) {
    int index = locked_path.lastIndexOf(ZK_PATH + "/");
    if (index >= 0) {
      index += ZK_PATH.length() + 1;
      return index <= locked_path.length() ? locked_path.substring(index) : "";
    }
    return null;

  }

  /**
   * 检查是否已经获取锁
   *
   * @param waiters
   *     节点下的所有等待着
   * @return ture 抢占到锁， false 未抢占到锁
   */
  private boolean checkLocked(List<String> waiters) {
    // 节点按照编号，升序排列
    Collections.sort(waiters);
    if (locked_short_path.equals(waiters.get(0))) {
      logger.info("成功获取分布式锁，节点为{}", locked_short_path);
      return true;
    }
    return false;
  }

  /**
   * 从zookeeper 中拿到所有等待节点
   */
  protected List<String> getWaiters() {
    List<String> children = null;
    try {
      children = client.getChildren().forPath(ZK_PATH);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return children;
  }

  private void await() throws Exception {
    if (null == prior_path) {
      throw new Exception("prior_path error");
    }
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    Watcher watcher = new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        logger.info("监听到的变化，watchEvent =" + event);
        countDownLatch.countDown();
      }
    };
    client.getData().usingWatcher(watcher).forPath(prior_path);

    /*
        //订阅比自己次小顺序节点的删除事件
        TreeCache treeCache = new TreeCache(client, prior_path);
        TreeCacheListener l = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client,
                                   TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                if (data != null) {
                    switch (event.getType()) {
                        case NODE_REMOVED:
                            log.debug("[TreeCache]节点删除, path={}, data={}",
                                    data.getPath(), data.getData());

                            latch.countDown();
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        treeCache.getListenable().addListener(l);
        treeCache.start();*/
    countDownLatch.await(WAIT_TIME, TimeUnit.SECONDS);
  }

}
