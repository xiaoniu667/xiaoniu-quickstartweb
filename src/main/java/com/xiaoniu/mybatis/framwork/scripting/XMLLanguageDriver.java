
package com.xiaoniu.mybatis.framwork.scripting;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.mapping.SqlSource;
import org.dom4j.Element;

public class XMLLanguageDriver implements LanguageDriver {


  @Override
  public SqlSource createSqlSource(Configuration configuration, Element element, Class<?> parameterType) {
    //用xml脚本构建器解析
    return new XMLScriptBuilder(configuration, element, parameterType).parseScriptNode();
  }
}
