package com.xiaoniu.mybatis.framwork.executor.resultset;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.executor.Executor;
import com.xiaoniu.mybatis.framwork.executor.result.DefaultResultContext;
import com.xiaoniu.mybatis.framwork.executor.result.DefaultResultHandler;
import com.xiaoniu.mybatis.framwork.executor.result.ResultHandler;
import com.xiaoniu.mybatis.framwork.executor.result.ResultSetWrapper;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.reflection.MetaClass;
import com.xiaoniu.mybatis.framwork.reflection.MetaObject;
import com.xiaoniu.mybatis.framwork.reflection.ReflectorFactory;
import com.xiaoniu.mybatis.framwork.reflection.factory.ObjectFactory;
import com.xiaoniu.mybatis.framwork.type.TypeHandler;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultResultSetHandler implements ResultSetHandler {
    private Executor executor;
    private MappedStatement mappedStatement;
    private ObjectFactory objectFactory;
    private DefaultResultHandler resultHandler;
    private final ReflectorFactory reflectorFactory;
    private Configuration configuration;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement) {
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        reflectorFactory = mappedStatement.getConfiguration().getReflectorFactory();
        this.objectFactory = mappedStatement.getConfiguration().getObjectFactory();
        this.configuration = mappedStatement.getConfiguration();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = (PreparedStatement) stmt;
        ResultSet resultSet = ps.getResultSet(); // 获取结果集  后续可以封装为ResultSetWrapper
        ResultSetWrapper rw = new ResultSetWrapper(resultSet, configuration);
        final List<Object> multipleResults = new ArrayList<>();
        Class<?> resultType = Class.forName(mappedStatement.getResultType());
        handlerResultSet(rw, resultType, multipleResults);
        return collapseSingleResultList(multipleResults);
//        return resultSetToObj(resultSet, mappedStatement);
    }

    private List<Object> collapseSingleResultList(List<Object> multipleResults) {
        return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
    }

    private void handlerResultSet(ResultSetWrapper rw, Class<?> resultType, List<Object> multipleResults) throws SQLException {
        //1.创建结果处理器
        resultHandler = new DefaultResultHandler(objectFactory);
        // 封装数据
        handlerRowValues(rw, resultType, resultHandler);
        // 保存结果
        multipleResults.add(resultHandler.getResultList());
    }

    //处理每一行的值
    private void handlerRowValues(ResultSetWrapper rw, Class<?> resultType, DefaultResultHandler resultHandler) throws SQLException {
        DefaultResultContext<Object> resultContext = new DefaultResultContext<>();
        while (rw.getResultSet().next()) {
            //得到结果
            Object rowValue = getRowValue(rw, resultType);
            //用resultHandler处理结果
            storeObject(resultHandler, resultContext, rowValue, rw);
        }
    }

    private void storeObject(DefaultResultHandler resultHandler, DefaultResultContext<Object> resultContext, Object rowValue, ResultSetWrapper rw) {
        resultContext.nextResultObject(rowValue);
        ((ResultHandler<Object>) resultHandler).handleResult(resultContext);
    }

    private Object getRowValue(ResultSetWrapper resultSetWrapper, Class<?> resultType) throws SQLException {
        Object rowValue = createResultObject(resultType);
        if (rowValue != null && !hasTypeHandlerForResultObject(resultType)) {
            final MetaObject metaObject = configuration.newMetaObject(rowValue);
            //对这个对象进行属性填充
            applyAutomaticMappings(resultSetWrapper, resultType, metaObject);
        }
        return rowValue;
    }

    //填充属性
    private void applyAutomaticMappings(ResultSetWrapper resultSetWrapper, Class<?> resultType, MetaObject metaObject) throws SQLException {
        List<String> columnNames = resultSetWrapper.getColumnNames();
        for (String columnName : columnNames) {
            //根据列的名字得到属性名
            String propertyName = metaObject.findProperty(columnName, false);
            //并且存在set方法
            if (propertyName != null && metaObject.hasSetter(propertyName)) {
                //得到set方法的中的类型
                Class<?> propertyType = metaObject.getSetterType(propertyName);
                //如果类型映射器中含有这个类型
                if (configuration.hasTypeHandler(propertyType)) {
                    //得到相应的处理器
                    TypeHandler<?> typeHandler = resultSetWrapper.getTypeHandlerMap().get(propertyType);
                    //用处理器得到结果a
                    final Object value = typeHandler.getResult(resultSetWrapper.getResultSet(), columnName);
                    //不是基本类型或者不是null
                    if (value != null || !propertyType.isPrimitive()) {
                        metaObject.setValue(propertyName, value);
                    }
                }
            }
        }

    }

    //得到类型处理器
    private boolean hasTypeHandlerForResultObject(Class<?> resultType) {
        return mappedStatement.getConfiguration().hasTypeHandler(resultType);
    }

    //创建要封装的对象
    private Object createResultObject(Class<?> resultType) {
        final MetaClass metaType = MetaClass.forClass(resultType, reflectorFactory);
        if (resultType.isInterface() || metaType.hasDefaultConstructor()) {
            // 普通的Bean对象类型 相当于newInstance
            return objectFactory.create(resultType);
        }
        throw new RuntimeException("Do not know how to create an instance of " + resultType);
    }

    private <T> List<T> resultSetToObj(ResultSet resultSet, MappedStatement mappedStatement) {
        ArrayList<T> list = new ArrayList<>();
        try {
            Class<?> resultType = Class.forName(mappedStatement.getResultType());
            ResultSetMetaData metaData = resultSet.getMetaData();
            //查询出来的列明 id name
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                T obj = (T) resultType.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    //值 1
                    //封装结果集
                    Object value = resultSet.getObject(i);
                    //列名 id
                    String columnName = metaData.getColumnName(i);
                    //调用obj的set方法进行set
                    //setId
                    String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method;
                    if (value instanceof Timestamp) {
                        //如果是时间戳
                        method = resultType.getMethod(setMethod, Date.class);
                    } else {
                        method = resultType.getMethod(setMethod, value.getClass());
                    }
                    //通过set方法设置相应的值
                    method.invoke(obj, value);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
}
