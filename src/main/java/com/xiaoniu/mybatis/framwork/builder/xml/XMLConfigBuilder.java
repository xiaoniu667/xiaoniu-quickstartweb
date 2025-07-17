package com.xiaoniu.mybatis.framwork.builder.xml;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.Environment;
import com.xiaoniu.mybatis.framwork.datasource.DataSourceFactory;
import com.xiaoniu.mybatis.framwork.io.Resource;
import com.xiaoniu.mybatis.framwork.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

//解析xml配置文件
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;


    public XMLConfigBuilder() {
        // 1. 调用父类初始化Configuration
        super(new Configuration());
    }

    public XMLConfigBuilder(Reader reader) {
        // 1. 调用父类初始化Configuration
        super(new Configuration());
        // 2. dom4j 处理 xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public Configuration parse() {
        try {
            //解析环境
            environmentElement(root.element("environments"));
            // 解析映射器
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;
    }

    /**
     * <environment id="development">
     * <transactionManager type="JDBC"/>
     * <dataSource type="DRUID">
     * <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
     * <property name="url" value="jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=utf8"/>
     * <property name="username" value="root"/>
     * <property name="password" value="zyw123456"/>
     * </dataSource>
     * </environment>
     */
    private void environmentElement(Element context) throws Exception {
        if (context == null) {
            return;
        }
        String environment = context.attributeValue("default");
        List<Element> environmentList = context.elements("environment");
        for (Element e : environmentList) {
            String id = e.attributeValue("id");
            if (id.equals(environment)) {
                //获取事务管理器
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(e.element("transactionManager").attributeValue("type")).newInstance();
                Element dataSourceElement = e.element("dataSource");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();
                List<Element> propertyList = dataSourceElement.elements("property");
                Properties props = new Properties();
                for (Element property : propertyList) {
                    props.setProperty(property.attributeValue("name"), property.attributeValue("value"));
                }
                dataSourceFactory.setProperty(props);
                DataSource dataSource = dataSourceFactory.getDataSource();
                // 构建环境
                Environment.Builder environmentBuilder = new Environment.Builder(id)
                        .transactionFactory(txFactory)
                        .dataSource(dataSource);
                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }

    private void mapperElement(Element mappers) throws Exception {
        //解析mapper
        List<Element> mapperList = mappers.elements("mapper");
        for (Element element : mapperList) {
            String resource = element.attributeValue("resource");
            //拿到实际的xml结点
            InputStream inputStream = Resource.getResourceAsStream(resource);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream, configuration, resource);
            xmlMapperBuilder.parse();
        }






//        List<Element> mapperList = mappers.elements("mapper");
//        for (Element element : mapperList) {
//            String resource = element.attributeValue("resource");
//            Reader reader = Resource.getResourceAsReader(resource);
//            SAXReader saxReader = new SAXReader();
//            Document document = saxReader.read(new InputSource(reader));
//            Element rootElement = document.getRootElement();
//            // 命名空间
//            String namespace = rootElement.attributeValue("namespace");
//            List<Element> selectNodes = rootElement.elements("select");
//            for (Element selectNode : selectNodes) {
//                //解析select中的语句
//                String id = selectNode.attributeValue("id");
//                String parameterType = selectNode.attributeValue("parameterType");
//                String resultType = selectNode.attributeValue("resultType");
//                //  select id, name from school where id = #{id}
//                String sql = selectNode.getTextTrim();
//                HashMap<Integer, String> parameter = new HashMap<>();
//                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
//                Matcher matcher = pattern.matcher(sql);
//                for (int i = 1; matcher.find(); i++) {
//                    String g1 = matcher.group(1);
//                    String g2 = matcher.group(2);
//                    parameter.put(i, g2);
//                    sql = sql.replace(g1, "?");
//                }
//                String msId = namespace + "." + id;
//                String name = selectNode.getName();
//                SqlCommandType sqlCommandType = SqlCommandType.valueOf(name.toUpperCase(Locale.ENGLISH));
//                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, parameterType, resultType, sql, parameter).build();
//                configuration.addMappedStatement(mappedStatement);
//            }
//            configuration.addMapper(Class.forName(namespace));
//        }

    }
}
