package com.xb.curator;

import com.xb.curator.snowFlakeID.SnowflakeIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName SnowflakeIdTest
 * @Description TODO
 * @Author xb
 * @Date 2021/11/24 14:44
 * @Version 1.0
 **/
public class SnowflakeIdTest {
  @Test
  public void  snowflakeIdTest(){

    SnowflakeIdGenerator.instance.init(1);
    final ExecutorService es = Executors.newFixedThreadPool(10);
     final HashSet set = new HashSet();
    Collections.synchronizedCollection(set);
    final long start = System.currentTimeMillis();
    for (int i = 0; i < 10; i++) {
      es.execute(()->{
        for (int j = 0; j < 5000000; j++) {
          final Long id = SnowflakeIdGenerator.instance.nextId();
          synchronized (id){
            set.add(id);
          }
        }
      });
    }
    es.shutdown();
    try {
      es.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("耗时"+ (end-start)+"ms");
  }
}
