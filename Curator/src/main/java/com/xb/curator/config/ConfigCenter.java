package com.xb.curator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>todo</p>
 *
 * @Author xb
 * @Date 2022/1/17 21:05
 * @Version 1.0
 **/
@Slf4j
public class ConfigCenter {
  private static final String ZK_ADDRESS = "192.168.56.101:2181";

  private final static Integer SESSION_TIMEOUT = 30 * 1000;

  private static ZooKeeper zooKeeper = null;


  private static CountDownLatch countDownLatch = new CountDownLatch(1);

  public static void main(String[] args) throws IOException, InterruptedException {
    zooKeeper = new ZooKeeper(ZK_ADDRESS, SESSION_TIMEOUT, new Watcher() {
      @Override
      public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.None && watchedEvent.getState() == Event.KeeperState.SyncConnected) {
          log.info("连接已建立");
          countDownLatch.countDown();
        }
      }
    });
    countDownLatch.await();

    MyConfig myConfig = new MyConfig();
    myConfig.setKey("key");
    myConfig.setName("name");
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] bytes = objectMapper.writeValueAsBytes(myConfig);

    Watcher watcher = new Watcher() {
      @SneakyThrows
      @Override
      public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDataChanged
            && event.getPath() != null
            && event.getPath().equals("/myConfig")) {
          log.info("path:{} 发生了数据变化", event.getPath());
          byte[] data = zooKeeper.getData(event.getPath(), this, null);
          MyConfig newConfig = objectMapper.readValue(new String(data), MyConfig.class);
          log.info("数据发生了变化{}", newConfig);
        }
      }
    };
    try {
      String s = zooKeeper.create("/myConfig", bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

      byte[] data = zooKeeper.getData("/myConfig", watcher, null);
      MyConfig originConfig = objectMapper.readValue(new String(data), MyConfig.class);
      log.info("原始数据{}", originConfig);
    } catch (KeeperException e) {
      e.printStackTrace();
    }

    TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);

  }

}
