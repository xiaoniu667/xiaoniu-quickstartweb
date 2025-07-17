package com.xiaoniu.springboot;

import com.xiaoniu.spring.XiaoNiuBean;
import com.xiaoniu.spring.XiaoNiuComponent;
import com.xiaoniu.spring.XiaoNiuConditional;

@XiaoNiuComponent
public class WebServerAutoConfiguration {

    @XiaoNiuBean
    @XiaoNiuConditional(TomcatCondition.class)
    public TomcatServer getTomcatServer() {
        return new TomcatServer();
    }


    @XiaoNiuBean
    @XiaoNiuConditional(JettyCondition.class)
    public JettyServer getJettyServer() {
        return new JettyServer();
    }
}
