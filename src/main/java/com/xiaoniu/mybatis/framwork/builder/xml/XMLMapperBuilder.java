package com.xiaoniu.mybatis.framwork.builder.xml;

import com.xiaoniu.mybatis.framwork.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

//映射构建器
public class XMLMapperBuilder extends BaseBuilder {
    private Element element;
    private String resource;
    private String currentNamespace;

    //将流转为document
    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) throws DocumentException {
        this(new SAXReader().read(inputStream), configuration, resource);
    }

    public XMLMapperBuilder(Document document, Configuration configuration, String resource) {
        super(configuration);
        //拿到mapper.xml的节点
        this.element = document.getRootElement();
        this.resource = resource;
    }


    //解析xml中的文件
    public void parse() throws ClassNotFoundException {
        if (!configuration.isResourceLoaded(resource)) {
            //处理mapper中的信息
            configurationElement(element);
            //标记一下
            configuration.addLoadedResource(resource);
            //namespace注册mapper
            configuration.addMapper(Class.forName(currentNamespace));
        }
    }

    private void configurationElement(Element element) {
        //1.配置namespace
        this.currentNamespace = element.attributeValue("namespace");
        if (currentNamespace.equals("")) {
            throw new RuntimeException("Mapper's namespace cannot be empty");
        }
        //2.配置select/update/delete/insert
        buildStatementFromContext(element.elements("select"));
        buildStatementFromContext(element.elements("update"));
        buildStatementFromContext(element.elements("insert"));
        buildStatementFromContext(element.elements("delete"));
    }

    private void buildStatementFromContext(List<Element> list) {
        for (Element e : list) {
            XMLStatementBuilder xmlStatementBuilder = new XMLStatementBuilder(configuration, e, currentNamespace);
            xmlStatementBuilder.parseStatementNode();
        }
    }

}
