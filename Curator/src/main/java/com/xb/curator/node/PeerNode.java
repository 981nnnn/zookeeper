package com.xb.curator.node;

import org.apache.curator.framework.CuratorFramework;

/**
 * @ClassName PeerNode
 * @Description 集群节点的命名服务
 * @Author xb
 * @Date 2021/11/24 13:15
 * @Version 1.0
 **/
public class PeerNode {
  // Zookeeper 客户端
  private CuratorFramework client = null;
  private String pathRegistered = null;
  private static PeerNode singleInstance = null;


}
