package com.haha.webflux.factory;

import com.haha.webflux.enums.FlowChatSceneEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/***
 * @ClassName ChatFactory
 * @Description 流式问答工厂
 */
@Component
public class FlowChatFactory implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private static final Map<FlowChatSceneEnum, IFlowChat> SERVICES = new HashMap<>();

    /**
     * 根据枚举获取实现类实例
     */
    public IFlowChat getInstance(FlowChatSceneEnum chatEnum) {
        return SERVICES.get(chatEnum);
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, IFlowChat> beans = applicationContext.getBeansOfType(IFlowChat.class);
        for (IFlowChat bean : beans.values()) {
            SERVICES.put(bean.getFlowChatEnum(), bean);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
