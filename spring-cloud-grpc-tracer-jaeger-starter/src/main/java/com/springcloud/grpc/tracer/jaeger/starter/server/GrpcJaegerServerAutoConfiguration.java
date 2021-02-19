package com.springcloud.grpc.tracer.jaeger.starter.server;

import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
@Configuration
@ConditionalOnProperty(name = "opentracing.jaeger.enable",havingValue = "true")
public class GrpcJaegerServerAutoConfiguration {

    @Autowired
    private Tracer tracer;

    @Bean
    GrpcJaegerServerInterceptor getGrpcJaegerServerInterceptor() {
        return new GrpcJaegerServerInterceptor(tracer);
    }


}
