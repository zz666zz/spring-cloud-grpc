package com.springcloud.grpc.server.core.starter.interceptor;

import io.grpc.ServerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.Map;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class SpringContextGlobalGrpcServerInterceptorConfigurer implements GlobalGrpcServerInterceptorConfigurer {

    @Autowired
    private ApplicationContext context;

    @Override
    public void config(GlobalGrpcServerInterceptorRegistry registry) {
        Map<String, ServerInterceptor> map = context.getBeansOfType(ServerInterceptor.class);
        Iterator iterator = map.values().iterator();
        while(iterator.hasNext()) {
            ServerInterceptor serverInterceptor = (ServerInterceptor) iterator.next();
            registry.addServerInterceptors(serverInterceptor);
        }
    }
}
