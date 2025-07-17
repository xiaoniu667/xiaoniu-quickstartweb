
package com.xiaoniu.mybatis.framwork.executor.result;

import com.xiaoniu.mybatis.framwork.reflection.factory.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

//这里封装了一个非常简单的结果集对象，默认情况下都会写入到这个对象的 list 集合中
public class DefaultResultHandler implements ResultHandler<Object> {

  private final List<Object> list;

  public DefaultResultHandler() {
    list = new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  /**
   * 通过 ObjectFactory 反射工具类，产生特定的 List
   */
  public DefaultResultHandler(ObjectFactory objectFactory) {
    list = objectFactory.create(List.class);
  }

  @Override
  public void handleResult(ResultContext<?> context) {
    list.add(context.getResultObject());
  }

  public List<Object> getResultList() {
    return list;
  }

}
