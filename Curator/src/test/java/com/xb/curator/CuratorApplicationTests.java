package com.xb.curator;

import com.xb.curator.config.ClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CuratorApplicationTests {

  @Test
  void contextLoads() {
  }

  @Test
  public void createNode() {
    String ZK_ADDRESS = "192.168.56.101:2182";
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    try {
      client.start();
      String data = "hello";
      byte[] payLoad = data.getBytes("UTF-8");
      String zkPath = "/test/CRUD/node-1";
      client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkPath, payLoad);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.close();
    }
  }

  /** 读取节点 */
  @Test
  public void readNode() {     //创建客户端
    String ZK_ADDRESS = "192.168.56.101:2182";
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    try {         //启动客户端实例,连接服务器
      client.start();
      String zkPath = "/test/CRUD/node-1";
      Stat stat = client.checkExists().forPath(zkPath);
      if (null != stat) {
        //读取节点的数据
        byte[] payload = client.getData().forPath(zkPath);
        String data = new String(payload, "UTF-8");
        System.out.println(("read data:" + data));
        String parentPath = "/test/CRUD";
        List<String> children = client.getChildren().forPath(parentPath);
        for (String child : children) {
          System.out.println("child:" + child);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      CloseableUtils.closeQuietly(client);
    }
  }

  @Test
  public void updateNode(){
    String ZK_ADDRESS = "192.168.56.101:2182";
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    try {
      client.start();
      String  data = "hello data";
      byte[] payLoad = data.getBytes("UTF-8");
      String zkPath = "/test/CRUD/node-1";
      client.setData().forPath(zkPath,payLoad);
    }catch (Exception e){

    }
  }
}
