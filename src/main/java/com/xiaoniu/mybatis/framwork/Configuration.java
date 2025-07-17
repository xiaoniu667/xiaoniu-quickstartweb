package com.xiaoniu.mybatis.framwork;


import com.xiaoniu.mybatis.framwork.binding.MapperRegistry;
import com.xiaoniu.mybatis.framwork.datasource.DruidDataSourceFactory;
import com.xiaoniu.mybatis.framwork.executor.Executor;
import com.xiaoniu.mybatis.framwork.executor.parameter.DefaultParameterHandler;
import com.xiaoniu.mybatis.framwork.executor.parameter.ParameterHandler;
import com.xiaoniu.mybatis.framwork.executor.resultset.DefaultResultSetHandler;
import com.xiaoniu.mybatis.framwork.executor.resultset.ResultSetHandler;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.reflection.DefaultReflectorFactory;
import com.xiaoniu.mybatis.framwork.reflection.MetaObject;
import com.xiaoniu.mybatis.framwork.reflection.ReflectorFactory;
import com.xiaoniu.mybatis.framwork.reflection.factory.DefaultObjectFactory;
import com.xiaoniu.mybatis.framwork.reflection.factory.ObjectFactory;
import com.xiaoniu.mybatis.framwork.reflection.wrapper.DefaultObjectWrapperFactory;
import com.xiaoniu.mybatis.framwork.reflection.wrapper.ObjectWrapperFactory;
import com.xiaoniu.mybatis.framwork.scripting.LanguageDriverRegistry;
import com.xiaoniu.mybatis.framwork.scripting.XMLLanguageDriver;
import com.xiaoniu.mybatis.framwork.session.SqlSession;
import com.xiaoniu.mybatis.framwork.statement.PreparedStatementHandler;
import com.xiaoniu.mybatis.framwork.statement.StatementHandler;
import com.xiaoniu.mybatis.framwork.transaction.jdbc.JdbcTransactionFactory;
import com.xiaoniu.mybatis.framwork.type.TypeHandler;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Configuration {

    private Environment environment;
    protected HashSet<String> loadedResources = new HashSet<>();
    protected final LanguageDriverRegistry languageRegistry;
    protected final com.xiaoniu.mybatis.framwork.type.TypeAliasRegistry typeAliasRegistry = new com.xiaoniu.mybatis.framwork.type.TypeAliasRegistry();
    protected static Map<Class<?>, TypeHandler> typeHandlerMap = new HashMap<>();
    private final com.xiaoniu.mybatis.framwork.type.TypeHandler<Object> unknownTypeHandler = new com.xiaoniu.mybatis.framwork.type.UnknownTypeHandler(typeHandlerMap);
    /**
     * 映射注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);
    /**
     * 映射的语句，存储在Map中
     */
    protected Map<String, MappedStatement> mappedStatements = new HashMap<>();
    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    public Configuration() {
        //注册类型映射器
        typeHandlerMap.put(Object.class, unknownTypeHandler);
        typeHandlerMap.put(Integer.class, new com.xiaoniu.mybatis.framwork.type.IntegerTypeHandler());
        typeHandlerMap.put(int.class, new com.xiaoniu.mybatis.framwork.type.IntegerTypeHandler());
        typeHandlerMap.put(String.class, new com.xiaoniu.mybatis.framwork.type.StringTypeHandler());

        //注册相应的事务管理器和数据源 后续通过反射创建
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        //设置语言驱动器
        this.languageRegistry = new LanguageDriverRegistry();
        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
    }


    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public LanguageDriverRegistry getLanguageRegistry() {
        return this.languageRegistry;
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public <T> void addMapper(Class<T> type) {
        this.mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public <T> boolean hasMapper(Class<T> type) {
        return mapperRegistry.hasMapper(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public boolean hasStatement(String statementId) {
        return mappedStatements.containsKey(statementId);
    }

    public com.xiaoniu.mybatis.framwork.type.TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject) {
        //默认采用PreparedStatementHandler 真正需要根据配置文件选择的
        return new PreparedStatementHandler(executor, mappedStatement, parameterObject);
    }

    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement) {
        return new DefaultResultSetHandler(executor, mappedStatement);
    }

    public Map<Class<?>, com.xiaoniu.mybatis.framwork.type.TypeHandler> getTypeHandlerMap() {
        return typeHandlerMap;
    }

    public boolean hasTypeHandler(Class<?> parameterType) {
        return typeHandlerMap.containsKey(parameterType);
    }

    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, mappedStatement.getSqlSource().getBoundSql(parameterObject));
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }
}
