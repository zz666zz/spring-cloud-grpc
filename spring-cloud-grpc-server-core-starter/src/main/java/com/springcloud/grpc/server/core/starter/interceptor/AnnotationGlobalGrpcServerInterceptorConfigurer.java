package com.springcloud.grpc.server.core.starter.interceptor;

import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Set;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class AnnotationGlobalGrpcServerInterceptorConfigurer implements GlobalGrpcServerInterceptorConfigurer {
    private static final Logger log = LoggerFactory.getLogger(AnnotationGlobalGrpcServerInterceptorConfigurer.class);

    @Autowired
    private ApplicationContext context;

    @Override
    public void config(GlobalGrpcServerInterceptorRegistry registry) {
        Map<String, Object> map = this.context.getBeansWithAnnotation(GlobalGrpcServerInterceptor.class);
        Set<Map.Entry<String, Object>> set = map.entrySet();
        for (Map.Entry<String, Object> me : set) {
            String name = me.getKey();
            ServerInterceptor interceptor = (ServerInterceptor) me.getValue();
            log.debug("collected ServerInterceptor: " + name);
            registry.addServerInterceptors(interceptor);
        }
    }
}
