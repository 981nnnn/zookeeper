package com.xb.curator;

import com.xb.curator.ID.IDMarker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName IDMakerTester
 * @Description TODO
 * @Author xb
 * @Date 2021/11/24 11:40
 * @Version 1.0
 **/

public class IDMakerTester {


  @Test
  public void testMakeId() throws Exception {
    IDMarker marker = new IDMarker();
    String nodeName = "/test/IDMaker/ID-";
    for (int i = 0; i < 10; i++) {
      String id = marker.markId(nodeName);
      System.out.println("第" + i + "个创建的id为:" + id);
    }
  }
}
