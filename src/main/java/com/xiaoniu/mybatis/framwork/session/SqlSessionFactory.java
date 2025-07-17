package com.xiaoniu.mybatis.framwork.session;

public interface SqlSessionFactory {

    /**
     * 打开一个 session
     * @return SqlSession
     */
   SqlSession openSession();

    SqlSession openSession(boolean autoCommit);
}
