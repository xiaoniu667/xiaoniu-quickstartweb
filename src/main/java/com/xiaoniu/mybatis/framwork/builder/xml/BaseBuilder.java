package com.xiaoniu.mybatis.framwork.builder.xml;


import com.alibaba.druid.support.logging.Resources;
import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.type.TypeAliasRegistry;

import java.util.Locale;

public abstract class BaseBuilder {
    protected Configuration configuration;
    protected TypeAliasRegistry typeAliasRegistry;
    public BaseBuilder(Configuration configuration)
    {
        this.configuration = configuration;
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();
    }
    protected <T> Class<? extends T> resolveAlias(String string) {
        try {
            if (string == null) {
                return null;
            }
            // issue #748
            String key = string.toLowerCase(Locale.ENGLISH);
            Class<T> value;
            if (typeAliasRegistry.getTYPE_ALIASES().containsKey(key)) {
                value = (Class<T>) typeAliasRegistry.getTYPE_ALIASES().get(key);
            } else {
                value = (Class<T>) Resources.classForName(string);
            }
            return value;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
        }
    }
}
