package com.xiaoniu.mybatis.framwork.mapping;

//枚举类用于判断sql语句类型
public enum SqlCommandType {
    UNKNOWN,
    INSERT,
    UPDATE,
    DELETE,
    SELECT,
    FLUSH;

    private SqlCommandType() {
    }
}
