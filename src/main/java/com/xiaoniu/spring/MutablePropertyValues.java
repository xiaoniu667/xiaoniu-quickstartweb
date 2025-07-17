package com.xiaoniu.spring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MutablePropertyValues implements Iterable<PropertyValue> {
    //定义list集合对象，用来存储propertyValues对象
    private final List<PropertyValue> propertyValueList;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<>();
    }

    public MutablePropertyValues(List<PropertyValue> propertyValueList) {
        if (propertyValueList == null) {
            this.propertyValueList = new ArrayList<>();
        } else {
            this.propertyValueList = propertyValueList;
        }
    }

    /**
     * 判断集合是否为空
     */
    public boolean isEmpty() {
        return propertyValueList.isEmpty();
    }

    //添加property对象
    public MutablePropertyValues addPropertyValue(PropertyValue propertyValue) {
        for (int i = 0; i < propertyValueList.size(); i++) {
            PropertyValue currentPropertyValue = propertyValueList.get(i);
            if (currentPropertyValue.getName().equals(propertyValue.getName())) {
                propertyValueList.set(i, propertyValue);
                return this;
            }
        }
        this.propertyValueList.add(propertyValue);
        return this;
    }

    /*\
    判断是否有name指定属性值的对象
     */
    public boolean contains(String name) {
        for (PropertyValue propertyValue : propertyValueList) {
            if (propertyValue.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据name属性值获取property对象
     */
    public PropertyValue getPropertyValue(String name) {
        for (PropertyValue propertyValue : propertyValueList) {
            if (propertyValue.getName().equals(name)) {
                return propertyValue;
            }
        }
        return null;
    }


    /**
     * 获取所有的property对象
     */
    public PropertyValue[] getPropertyValues() {
        //指定返回数据的类型
        return propertyValueList.toArray(new PropertyValue[0]);
    }


    /**
     * 获取迭代器对象
     *
     * @return
     */
    @Override
    public Iterator<PropertyValue> iterator() {
        return propertyValueList.iterator();
    }
}
