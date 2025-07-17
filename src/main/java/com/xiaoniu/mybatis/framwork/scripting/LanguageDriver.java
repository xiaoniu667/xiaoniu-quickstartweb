
package com.xiaoniu.mybatis.framwork.scripting;


import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.mapping.SqlSource;
import org.dom4j.Element;

public interface LanguageDriver {

  SqlSource createSqlSource(Configuration configuration, Element element, Class<?> parameterType);
}
