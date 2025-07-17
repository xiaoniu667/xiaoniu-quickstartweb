package com.xiaoniu.mybatis.framwork.scripting;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.builder.xml.BaseBuilder;
import com.xiaoniu.mybatis.framwork.mapping.SqlSource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;


public class XMLScriptBuilder extends BaseBuilder {

    private final Element context;
    private boolean isDynamic;
    private final Class<?> parameterType;


    public XMLScriptBuilder(Configuration configuration, Element context, Class<?> parameterType) {
        super(configuration);
        this.context = context;
        this.parameterType = parameterType;
    }


    public SqlSource parseScriptNode() {
        List<SqlNode> sqlNodes = parseDynamicTags(context);
        MixedSqlNode mixedSqlNode = new MixedSqlNode(sqlNodes);
        return new RawSqlSource(configuration, mixedSqlNode, parameterType);
    }

    private List<SqlNode> parseDynamicTags(Element context) {
        ArrayList<SqlNode> sqlNodes = new ArrayList<>();
        String data = context.getTextTrim();
        sqlNodes.add(new StaticTextSqlNode(data));
        return sqlNodes;
    }


}
