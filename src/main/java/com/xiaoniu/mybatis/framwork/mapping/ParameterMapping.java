package com.xiaoniu.mybatis.framwork.mapping;


import com.xiaoniu.mybatis.framwork.Configuration;

public class ParameterMapping {

    private Configuration configuration;

    private String property;
    private Class<?> javaType = Object.class;

    private ParameterMapping() {
    }

    public static class Builder {
        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }
        public ParameterMapping build() {
            return parameterMapping;
        }

    }

    public String getProperty() {
        return property;
    }


    public Class<?> getJavaType() {
        return javaType;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParameterMapping{");
        sb.append("property='").append(property).append('\'');
        sb.append(", javaType=").append(javaType);
        sb.append('}');
        return sb.toString();
    }
}
