package com.xiaoniu.mybatis.framwork.builder.xml;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.mapping.SqlCommandType;
import com.xiaoniu.mybatis.framwork.mapping.SqlSource;
import com.xiaoniu.mybatis.framwork.scripting.LanguageDriver;
import org.dom4j.Element;

import java.util.Locale;

//语句构建器
public class XMLStatementBuilder extends BaseBuilder {
    private String currentNamespace;
    private Element element;

    public XMLStatementBuilder(Configuration configuration, Element e, String currentNamespace) {
        super(configuration);
        this.element = e;
        this.currentNamespace = currentNamespace;
    }
    //版本1
//    public void parseStatementNode() {
//        String id = element.attributeValue("id");
//        String parameterType = element.attributeValue("parameterType");
//        String resultType = element.attributeValue("resultType");
//        //  select id, name from school where id = #{id}
//        String sql = element.getTextTrim();
//        HashMap<Integer, String> parameter = new HashMap<>();
//        Pattern pattern = Pattern.compile("(#\\{(.*?)})");
//        Matcher matcher = pattern.matcher(sql);
//        for (int i = 1; matcher.find(); i++) {
//            String g1 = matcher.group(1);
//            String g2 = matcher.group(2);
//            parameter.put(i, g2);
//            sql = sql.replace(g1, "?");
//        }
//        String msId = currentNamespace + "." + id;
//        String name = element.getName();
//        SqlCommandType sqlCommandType = SqlCommandType.valueOf(name.toUpperCase(Locale.ENGLISH));
//        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, parameterType, resultType, sql, parameter).build();
//        configuration.addMappedStatement(mappedStatement);
//    }

    //版本2
    public void parseStatementNode() {
        //获取方法id
        String id = element.attributeValue("id");
        //获取参数类型
        String parameterType = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveClass(parameterType);
        //获取返回值类型
        String resultType = element.attributeValue("resultType");
        //得到sql类型
        String name = element.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(name.toUpperCase(Locale.ENGLISH));
        //msId  全类名+方法名字
        String msId = currentNamespace + "." + id;
        //获取sql
//        String sql = element.getTextTrim();
        //利用语言驱动器得到真实要执行的sql
        //执行完之后会将sql变为预执行的样子 sqlSource
        Class<? extends LanguageDriver> driverClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        LanguageDriver languageDriver = configuration.getLanguageRegistry().getDriver(driverClass);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, element, parameterTypeClass);
        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, resultType, sqlSource).build();
        configuration.addMappedStatement(mappedStatement);
    }

    private Class<?> resolveClass(String parameterType) {
        return resolveAlias(parameterType);
    }

}
