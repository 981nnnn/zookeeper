package com.xb.curator.common.concurrent;

/**
 * @ClassName CallbackTask
 * @Description TODO
 * @Author xb
 * @Date 2021/11/28 20:44
 * @Version 1.0
 **/
public interface CallbackTask<R> {
  R execute();

  void onSuccess(R r);

  void onFailure(Throwable t);
}
