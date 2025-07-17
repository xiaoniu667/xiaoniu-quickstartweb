package com.xiaoniu.mybatis.framwork.builder.xml;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.builder.ParameterExpression;
import com.xiaoniu.mybatis.framwork.builder.StaticSqlSource;
import com.xiaoniu.mybatis.framwork.mapping.ParameterMapping;
import com.xiaoniu.mybatis.framwork.mapping.SqlSource;
import com.xiaoniu.mybatis.framwork.parse.GenericTokenParser;
import com.xiaoniu.mybatis.framwork.parse.TokenHandler;
import com.xiaoniu.mybatis.framwork.reflection.MetaClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SqlSourceBuilder extends BaseBuilder {


    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    public SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql); //去掉#{} 转为?
        return new StaticSqlSource(configuration, sql, handler.getParameterMappings());
    }

    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        //将来在设置参数的时候根据这里面的顺序得到参数名称去map中获取即可
        private List<ParameterMapping> parameterMappings = new ArrayList<>();
        private Class<?> parameterType;

        public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType, Map<String, Object> additionalParameters) {
            super(configuration);
            this.parameterType = parameterType;
        }

        public List<ParameterMapping> getParameterMappings() {
            return parameterMappings;
        }

        @Override
        public String handleToken(String content) {
            parameterMappings.add(buildParameterMapping(content));
            return "?";
        }

        private ParameterMapping buildParameterMapping(String content) {
            Map<String, String> propertiesMap = new ParameterExpression(content);
            String property = propertiesMap.get("property");
            Class<?> propertyType =null;
            if (configuration.hasTypeHandler(parameterType)) {
                propertyType = parameterType;
            }else if (property != null) {
                MetaClass metaClass = MetaClass.forClass(parameterType,configuration.getReflectorFactory());
                if (metaClass.hasGetter(property)) {
                    propertyType = metaClass.getGetterType(property);
                } else {
                    propertyType = Object.class;
                }
            } else {
                propertyType = Object.class;
            }
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
            return builder.build();
        }

    }

}
