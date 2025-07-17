package com.xiaoniu.mybatis.framwork.session;

import com.xiaoniu.mybatis.framwork.Configuration;

import java.sql.SQLException;
import java.util.List;

public interface SqlSession {

    /**
     * Retrieve a single row mapped from the statement key
     * 根据指定的SqlID获取一条记录的封装对象
     *
     * @param <T>       the returned object type 封装之后的对象类型
     * @param statement sqlID
     * @return Mapped object 封装之后的对象
     */
    <T> T selectOne(String statement) throws Exception;

    /**
     * Retrieve a single row mapped from the statement key and parameter.
     * 根据指定的SqlID获取一条记录的封装对象，只不过这个方法容许我们可以给sql传递一些参数
     * 一般在实际使用中，这个参数传递的是pojo，或者Map或者ImmutableMap
     *
     * @param <T>       the returned object type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return Mapped object
     */
    <T> T selectOne(String statement, Object parameter) throws Exception;

    /**
     * Retrieves a mapper.
     * 得到映射器，这个巧妙的使用了泛型，使得类型安全
     *
     * @param <T>  the mapper type
     * @param type Mapper interface class
     * @return a mapper bound to this SqlSession
     */
    <T> T getMapper(Class<T> type);


    Configuration getConfiguration();


    <E> List<E> selectList(String statement) throws Exception;

    <E> List<E> selectList(String statement, Object parameter) throws Exception;

    int insert(String statement) throws SQLException;


    int insert(String statement, Object parameter) throws SQLException;

    int update(String statement) throws SQLException;


    int update(String statement, Object parameter) throws SQLException;

    int delete(String statement) throws SQLException;

    int delete(String statement, Object parameter) throws SQLException;

    void commit();

    /**
     * Flushes batch statements and commits database connection.
     * @param force forces connection commit
     */
    void commit(boolean force);
}
