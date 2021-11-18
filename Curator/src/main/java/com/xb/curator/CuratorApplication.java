package com.xb.curator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class CuratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(CuratorApplication.class, args);
  }

}
