package com.xb.curator.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Component;

/**
 * @ClassName ClientFactory
 * @Description TODO
 * @Author xb
 * @Date 2021/11/17 23:12
 * @Version 1.0
 **/
@Component
public class ClientFactory {
  /**
   *
   * @param connectionString 的连接地址
   * @return CuratorFramework 实例
   */
  public static CuratorFramework createSimple(String connectionString){
    /**
     * 重试策略：第一次重试等待1s,第二次重试等待2s，第三次重试等待3s
     * 第一个参数：等待时间的基础单位，单位毫秒
     * 第二个参数：最大重试次数
     */
    ExponentialBackoffRetry retry = new ExponentialBackoffRetry(100, 3);
    /**
     * 获取CuratorFramework 实例方式
     * 第一个参数：zk的连接地址
     * 第二个参数：重试策略
     */
    return CuratorFrameworkFactory.newClient(connectionString,retry);
  }

  /**
   *
   * @param connectionString zk 连接地址
   * @param retryPolicy   重试策略
   * @param connectionTimeoutMs   连接超时时间
   * @param sessionTimeoutMs  会话超时时间
   * @return CuratorFramework 实例
   */
  public static CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy,int connectionTimeoutMs,int sessionTimeoutMs){
    //用builder 方法创建CuratorFramework 实例
    return CuratorFrameworkFactory.builder()
        .connectString(connectionString)
        .retryPolicy(retryPolicy)
        .connectionTimeoutMs(connectionTimeoutMs)
        .sessionTimeoutMs(sessionTimeoutMs)
        .build();
  }
}
