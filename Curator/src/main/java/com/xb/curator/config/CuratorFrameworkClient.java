package com.xb.curator.config;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @ClassName CuratorFramework
 * @Description TODO
 * @Author xb
 * @Date 2021/11/24 11:32
 * @Version 1.0
 **/
@Configuration
public class CuratorFrameworkClient {
  @Bean
  public CuratorFramework createLient() {
    String ZK_ADDRESS = "192.168.56.101:2182";
    CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
    return client;
  }
}
