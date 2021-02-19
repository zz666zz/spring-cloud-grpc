package com.springcloud.grpc.client.core.starter.interceptor;

import io.grpc.ClientInterceptor;
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
public class AnnotationGlobalGrpcClientInterceptorConfigurer implements GlobalGrpcClientInterceptorConfigurer {
    private static final Logger log = LoggerFactory.getLogger(AnnotationGlobalGrpcClientInterceptorConfigurer.class);

    @Autowired
    private ApplicationContext context;

    @Override
    public void config(GlobalGrpcClientInterceptorRegistry registry) {
        Map<String, Object> map = this.context.getBeansWithAnnotation(GlobalGrpcClientInterceptor.class);
        Set<Map.Entry<String, Object>> set = map.entrySet();
        for (Map.Entry<String, Object> me : set) {
            String name = me.getKey();
            ClientInterceptor interceptor = (ClientInterceptor) me.getValue();
            log.debug("collected ServerInterceptor: " + name);
            registry.addClientInterceptors(interceptor);
        }
    }
}
