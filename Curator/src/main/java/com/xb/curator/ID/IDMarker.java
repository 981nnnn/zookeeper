package com.xb.curator.ID;

import com.xb.curator.config.ClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName IDMarker
 * @Description 生成分布式ID
 * @Author xb
 * @Date 2021/11/24 11:28
 * @Version 1.0
 **/
@Component
public class IDMarker {

  public CuratorFramework createClient(){
    String ZK_ADDRESS = "192.168.56.101:2182";
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    client.start();
    return client;
  }

  /**
   * 创建临时顺序节点
   *
   * @param pathPrfix
   *     节点路径
   */
  private String createSeqNode(String pathPrfix) throws Exception {
    final String destPath = this.createClient().create()
        .creatingParentsIfNeeded()
        .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
        .forPath(pathPrfix);
    return destPath;
  }

  public String markId(String nodeName) throws Exception {
    final String str = createSeqNode(nodeName);
    if (str == null) {
      return null;
    }
    int index = str.lastIndexOf(nodeName);
    if (index > 0) {
      index += nodeName.length();
      return index <= str.length() ? str.substring(index) : "";
    }
    return str;
  }
}
