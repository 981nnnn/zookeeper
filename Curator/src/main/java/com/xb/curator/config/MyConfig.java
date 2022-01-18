package com.xb.curator.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>todo</p>
 *
 * @Author xb
 * @Date 2022/1/17 21:28
 * @Version 1.0
 **/

@Data
@ToString
@NoArgsConstructor
public class MyConfig {
  private String key;
  private String name;
}
