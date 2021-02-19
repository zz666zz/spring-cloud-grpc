package com.springcloud.grpc.client.core.starter.interceptor;

import io.grpc.ClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.Map;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class SpringContextGlobalGrpcClientInterceptorConfigurer implements GlobalGrpcClientInterceptorConfigurer {

    @Autowired
    private ApplicationContext context;

    @Override
    public void config(GlobalGrpcClientInterceptorRegistry registry) {
        Map<String, ClientInterceptor> map = context.getBeansOfType(ClientInterceptor.class);
        Iterator iterator = map.values().iterator();
        while(iterator.hasNext()) {
            ClientInterceptor clientInterceptor = (ClientInterceptor) iterator.next();
            registry.addClientInterceptors(clientInterceptor);
        }
    }
}
