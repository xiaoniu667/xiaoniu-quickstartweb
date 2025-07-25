
package com.xiaoniu.mybatis.framwork.reflection.wrapper;

import com.xiaoniu.mybatis.framwork.reflection.MetaObject;
import com.xiaoniu.mybatis.framwork.reflection.factory.ObjectFactory;
import com.xiaoniu.mybatis.framwork.reflection.property.PropertyTokenizer;

import java.util.List;


public interface ObjectWrapper {

  Object get(PropertyTokenizer prop);

  void set(PropertyTokenizer prop, Object value);

  String findProperty(String name, boolean useCamelCaseMapping);

  String[] getGetterNames();

  String[] getSetterNames();

  Class<?> getSetterType(String name);

  Class<?> getGetterType(String name);

  boolean hasSetter(String name);

  boolean hasGetter(String name);

  MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

  boolean isCollection();

  void add(Object element);

  <E> void addAll(List<E> element);

}
