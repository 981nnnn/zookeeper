package com.xb.curator.config;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * The type Zk client.
 */

public class ZKClient {
  private static Logger logger = LoggerFactory.getLogger(ZKClient.class);

  private CuratorFramework client;

  private static final String ZK_ADDRESS= "192.168.56.101:2182";

  public CuratorFramework getClient(){
    return this.client;
  }

  /**
   * The constant instance.
   */
  public static ZKClient instance = null;

  static  {
    instance = new ZKClient();
    instance.init();
  }

  private ZKClient(){

  }

  /**
   * 初始化一个客户端，并启动客户端
   */
  public void init(){
    if (client != null) {
      return;
    }
    // 创建一个客户端
    client = ClientFactory.createSimple(ZK_ADDRESS);
    // 启动客户端实例，连接服务器
    client.start();
  }

  /**
   * 销毁一个客户端
   */
  public void destroy(){
    this.client.close();
  }

  /**
   * 创建一个结点
   *
   * @param zkPath
   *     节点的路径
   * @param data
   *     数据
   */
  public void createNode(String zkPath,String data){
    try {
      byte[] payLoad = "to set content".getBytes("UTF-8");
      if (data != null) {
        payLoad = data.getBytes("UTF-8");
        client.create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.PERSISTENT)
            .forPath(zkPath,payLoad);

      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Delete node.
   *
   * @param zkPath
   *     the zk path
   */
  public void deleteNode(String zkPath){
    if (!isNodeExist(zkPath)) {
      return ;
    }
    try {
      client.delete().forPath(zkPath);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 判断一个节点是否存在
   *
   * @param zkPath
   *     路径
   * @return the boolean
   */
  public boolean isNodeExist(String zkPath){
    Stat stat = null;
    try {
      stat = client.checkExists().forPath(zkPath);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (stat==null) {
      logger.info("节点不存在",zkPath);
      return  false;
    }else {
      logger.info("节点存在 stat is ", stat.toString());
      return true;
    }
  }

  /**
   * Create ephemeral seq node string.
   *
   * @param srcPath
   *     the src path
   * @param mode
   *     the mode
   * @return the string
   */
  public String createEphemeralSeqNode(String srcPath,CreateMode mode){
    try {
      String path = client.create()
          .creatingParentsIfNeeded()
          .withMode(mode)
          .forPath(srcPath);
      return path;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}