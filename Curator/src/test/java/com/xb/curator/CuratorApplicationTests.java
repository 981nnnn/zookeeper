package com.xb.curator;

import com.xb.curator.config.ClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
class CuratorApplicationTests {

  private static final String ZK_ADDRESS ="192.168.56.101:2182" ;

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
      e.printStackTrace();
    }finally {
      CloseableUtils.closeQuietly(client);
    }
  }

  @Test
  public void updateNodeAsync(){
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    try {
      AsyncCallback.StringCallback callback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int i, String s, Object o, String s1) {
          System.out.println(
              "i = " + i + " | " +
                  "s = " + s + " | " +
                  "o = " + o + " | " +
                  "s1 = " + s1);
        }
      };
      client.start();
      String data = "helLo world";
      byte[] payLoad = data.getBytes("UTF-8");
      String zkPath = "/test/CRUD/remoteNode-1";
      client.setData().inBackground(callback).forPath(zkPath,payLoad);
      Thread.sleep(10000);
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      CloseableUtils.closeQuietly(client);
    }

  }


  @Test
  public void deleteNode(){
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    try{
      client.start();
      String zkPath = "/test/CRUD/remoteNode-1";
      client.delete().forPath(zkPath);
      String parentPath = "/test";
      List<String> list1 = client.getChildren().forPath(parentPath);
      for (String s : list1) {
        System.out.println(s);
      }
      HashMap hashMap = new HashMap();

    }catch (Exception e){
      e.printStackTrace();
    }finally {
      CloseableUtils.closeQuietly(client);
    }
  }


  @Test
  public void test(){
    Integer a = new Integer(1);
    Integer b = new Integer(1);
    Integer c = 1;
    System.out.println(a==b); // false 会在内存中开辟一块内存空间
    System.out.println(a.equals(b)); // Integer 重写了equals 方法
    Integer aa = Integer.valueOf(a); // 如果在-128到127 中，会从IntegerCache中获取，否则new Integer
    System.out.println(aa==a);  // false a 与 aa 不是指向同一个堆内存空间，是两个new出来的对象
    System.out.println(aa==c);  // true aa = c ，都是IntegerCache中的值
    System.out.println(a==c);   //  false
    System.out.println(a.equals(c));  // true
    System.out.println("------------");
    Integer i = 1;
    Integer j = 1;
    System.out.println(i==j);  // true ， IntegerCache 缓冲池中获取
    System.out.println(i.equals(j)); // ture
    System.out.println("-=-=-====");
    Integer z = 9999;
    Integer y = 9999;
    System.out.println(z==y);  // false ，超过了-128到127 ，都是去new Integer()
    System.out.println(z.equals(y)); // false

    System.out.println("!!!!!!!!!!!!!");
    int  a1 = 1;
    Integer a2 = new Integer(1);
    System.out.println(a1==a2);  // 自动拆装箱


    int a22=2220;
    double b22=2220.0;
    System.out.println(a22==b22); // ??
  }


  @Test
  public void test02() throws InterruptedException {
    ReentrantLock lock = new ReentrantLock();

    lock.tryLock(1, TimeUnit.SECONDS);

    lock.lock();
  }


}
