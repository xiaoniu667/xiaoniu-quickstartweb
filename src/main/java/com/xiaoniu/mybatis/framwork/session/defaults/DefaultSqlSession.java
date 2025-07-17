package com.xiaoniu.mybatis.framwork.session.defaults;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.executor.Executor;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.session.SqlSession;

import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    /**
     * 映射器注册机
     */
    private Configuration configuration;

    private Executor executor;

    private final boolean autoCommit;

    public DefaultSqlSession(Configuration configuration, Executor executor,boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.autoCommit =autoCommit;
    }

    @Override
    public <T> T selectOne(String statement) throws Exception {
        return this.selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) throws Exception {
        MappedStatement ms = configuration.getMappedStatement(statement);
        List<T> list = executor.query(ms, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <E> List<E> selectList(String statement) throws Exception {
        return this.selectList(statement, null);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) throws Exception {
        MappedStatement ms = configuration.getMappedStatement(statement);
        return executor.query(ms, parameter);
    }

    @Override
    public int insert(String statement) throws SQLException {
        return this.insert(statement, null);
    }

    @Override
    public int insert(String statement, Object parameter) throws SQLException {
        MappedStatement ms = configuration.getMappedStatement(statement);
        return executor.update(ms, parameter);
    }

    @Override
    public int update(String statement) throws SQLException {
        return this.update(statement, null);
    }

    @Override
    public int update(String statement, Object parameter) throws SQLException {
        MappedStatement ms = configuration.getMappedStatement(statement);
        return executor.update(ms, parameter);
    }

    @Override
    public int delete(String statement) throws SQLException {
        return this.delete(statement, null);
    }

    @Override
    public int delete(String statement, Object parameter) throws SQLException {
        MappedStatement ms = configuration.getMappedStatement(statement);
        return executor.update(ms, parameter);
    }

    @Override
    public void commit() {
        commit(false);
    }

    @Override
    public void commit(boolean force) {
        try {
            executor.commit(!autoCommit);
        } catch (Exception e) {
            throw new RuntimeException("Error committing transaction.  Cause: " + e, e);
        }
    }
}

//把数据源的配置加载进来以后，就可以把 SQL 语句放到数据源中进行执行以及结果封装
//        return (T) ("你被代理了！" + "方法：" + statement + " 入参：" + parameter + "待执行的sql为" + mappedStatement.getSql());
//1.得到映射语句 里面包含了sql语句以及参数信息
//        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
//        Environment environment = configuration.getEnvironment();
//        Connection connection = null;
//        try {
//            //2.获取数据库连接  DruidDataSource
//            connection = environment.getDataSource().getConnection();
//            //3.预编译sql
//            PreparedStatement preparedStatement = connection.prepareStatement(mappedStatement.getSql());
//            //4.设置参数
//            preparedStatement.setString(1, String.valueOf(((Object[]) parameter)[0].toString()));
//            //5.执行sql
//            ResultSet resultSet = preparedStatement.executeQuery();
//            //6.封装结果
//            List<T> objList = resultSetToObj(resultSet, Class.forName(mappedStatement.getResultType()));
//            return objList.get(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;

//    private <T> List<T> resultSetToObj(ResultSet resultSet, Class<?> resultType) {
//        ArrayList<T> list = new ArrayList<>();
//        try {
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            //查询出来的列明 id name
//            int columnCount = metaData.getColumnCount();
//            while (resultSet.next()) {
//                T obj = (T) resultType.newInstance();
//                for (int i = 1; i <= columnCount; i++) {
//                    //值 1
//                    Object value = resultSet.getObject(i);
//                    //列名 id
//                    String columnName = metaData.getColumnName(i);
//                    //调用obj的set方法进行set
//                    //setId
//                    String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
//                    Method method;
//                    if (value instanceof Timestamp) {
//                        //如果是时间戳
//                        method = resultType.getMethod(setMethod, Date.class);
//                    } else {
//                        method = resultType.getMethod(setMethod, value.getClass());
//                    }
//                    //通过set方法设置相应的值
//                    method.invoke(obj, value);
//                }
//                list.add(obj);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
