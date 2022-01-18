package com.xb.curator;

import com.xb.curator.config.ClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;


/**
 * @ClassName ZkWatcherTest
 * @Description TODO
 * @Author xb
 * @Date 2021/11/24 15:39
 * @Version 1.0
 **/
public class ZkWatcherTest {

  private static final String ZK_ADDRESS = "192.168.56.101:2182";

  private final String workerPath = "/test/listener/remoteNode";
  private final String subWorkerPath = "/test/listener/remoteNode/id-";

  @Test
  public void testWatcher() throws Exception {
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    client.start();
    // 判断节点是否存在
    Stat stat = client.checkExists().forPath(workerPath);
    if (stat == null) {
      // 创建节点
      client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(workerPath);
    }
    try {
      Watcher watch = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
          System.out.println("监听到的变化=" + watchedEvent);
        }
      };
      final byte[] content = client.getData().usingWatcher(watch).forPath(workerPath);
      System.out.println("监听节点内容" + new String(content));
      // 第一次变更节点数据
      client.setData().forPath(workerPath, "第一次更改内容".getBytes());
      // 第二次变更节点数据
      client.setData().forPath(workerPath, "第二次更改内容".getBytes());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testNodeCache() throws Exception {
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    client.start();
    // 判断节点是否存在
    Stat stat = client.checkExists().forPath(workerPath);

    NodeCache nodeCache = new NodeCache(client, workerPath, false);
    NodeCacheListener listener = new NodeCacheListener() {
      @Override
      public void nodeChanged() throws Exception {
        ChildData childData = nodeCache.getCurrentData();
        System.out.println("ZNode节点状态改变, path={}" + childData.getPath());
        System.out.println("ZNode节点状态改变, data={}" + new String(childData.getData(), StandardCharsets.UTF_8));
        System.out.println("ZNode节点状态改变, stat={}" + childData.getStat());
      }
    };
    // 启动节点的事件监听
    nodeCache.getListenable().addListener(listener);
    nodeCache.start();
    // 第一次变更节点数据
    client.setData().forPath(workerPath,"第一次更改内容".getBytes());
    Thread.sleep(1000);
    // 第二次变更节点数据
    client.setData().forPath(workerPath,"第二次更改内容".getBytes());
    Thread.sleep(1000);
    // 第三次变更节点数据
    client.setData().forPath(workerPath,"第三次更改内容".getBytes());
    Thread.sleep(1000);
    // 第四次变更节点数据
    client.setData().forPath(workerPath,"第四次更改内容".getBytes());
    Thread.sleep(Integer.MAX_VALUE);

  }

  @Test
  public void testPathChildrenCache() throws Exception {
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    // 启动客户端
    client.start();
    //判断节点是否存在
    Stat stat = client.checkExists().forPath(workerPath);
    if(stat==null){
      // 创建节点
      client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(workerPath);
    }
    PathChildrenCache cache = new PathChildrenCache(client, workerPath, true);

    PathChildrenCacheListener listener = new PathChildrenCacheListener() {

      @Override
      public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        ChildData data = event.getData();
        switch (event.getType()){
          case CHILD_ADDED:
            System.out.println("子节点增加"+data.getPath()+ "--"+new String(data.getData(), "UTF-8"));
            break;
          case CHILD_UPDATED:
            System.out.println("子节点更新"+data.getPath()+ "--"+new String(data.getData(), "UTF-8"));
            break;
          case CHILD_REMOVED:
            System.out.println("子节点删除"+data.getPath()+ "--"+new String(data.getData(), "UTF-8"));
            break;

          default:
            break;
        }
      }
    };
    // 添加监视器
    cache.getListenable().addListener(listener);
    // 设置启动模式
    cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    // 添加三个子节点
    for (int i = 0; i < 3; i++) {
      client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(subWorkerPath+i);
    }
    // 删除三个子节点
    for (int i = 0; i < 3; i++) {
      client.delete().forPath(subWorkerPath+i);
    }
  }

  @Test
  public void testTreeCache() throws Exception {
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    client.start();
    Stat stat = client.checkExists().forPath(workerPath);
    if(stat==null){
      client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(workerPath);
    }

    TreeCache treeCache = new TreeCache(client, workerPath);
    TreeCacheListener listener = new TreeCacheListener() {

      @Override
      public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        ChildData data = event.getData();
        if (data == null) {
          System.out.println("数据空");
          return;
        }
        switch (event.getType()) {
          case NODE_ADDED:
            System.out.println("节点增加" + data.getPath() + "--" + new String(data.getData(), "UTF-8"));
            break;
          case NODE_UPDATED:
            System.out.println("节点更新" + data.getPath() + "--" + new String(data.getData(), "UTF-8"));
            break;
          case NODE_REMOVED:
            System.out.println("节点删除" + data.getPath() + "--" + new String(data.getData(), "UTF-8"));
            break;
        }

      }
    };
    // 设置监控器
    treeCache.getListenable().addListener(listener);
    // 启动缓存视图
    treeCache.start();
    Thread.sleep(1000);
    // 创建3个子节点
    for (int i = 0; i < 3; i++) {
      client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(subWorkerPath+i);
    }
    Thread.sleep(1000);
    // 删除3个子节点
    for (int i = 0; i < 3; i++) {
      client.delete().forPath(subWorkerPath+i);
    }
    Thread.sleep(1000);
    // 删除当前节点
    client.delete().forPath(workerPath);
    Thread.sleep(Integer.MAX_VALUE);
  }
}
