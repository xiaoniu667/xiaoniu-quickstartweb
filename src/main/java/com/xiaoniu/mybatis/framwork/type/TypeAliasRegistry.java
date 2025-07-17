package com.xiaoniu.mybatis.framwork.type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 在 Mybatis 框架中我们所需要的基本类型、数组类型以及自己定义的事务实现和事务工厂都需要注册到类型别名的注册器中进行管理，
 * 在我们需要使用的时候可以从注册器中获取到具体的对象类型，之后在进行实例化的方式进行使用。
 */
public class TypeAliasRegistry {

    private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<>();

    public TypeAliasRegistry() {
        // 构造函数里注册系统内置的类型别名
        registerAlias("string", String.class);

        // 基本包装类型
        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);
    }

    public void registerAlias(String alias, Class<?> value) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        TYPE_ALIASES.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> resolveAlias(String string) {
        String key = string.toLowerCase(Locale.ENGLISH);
        return (Class<T>) TYPE_ALIASES.get(key);
    }

    public Map<String, Class<?>> getTYPE_ALIASES() {
        return TYPE_ALIASES;
    }
}
