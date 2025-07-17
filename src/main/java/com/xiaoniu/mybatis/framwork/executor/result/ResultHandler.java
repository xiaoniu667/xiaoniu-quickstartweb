
package com.xiaoniu.mybatis.framwork.executor.result;

/**
 * @author Clinton Begin
 */
public interface ResultHandler<T> {

  void handleResult(ResultContext<? extends T> resultContext);

}
