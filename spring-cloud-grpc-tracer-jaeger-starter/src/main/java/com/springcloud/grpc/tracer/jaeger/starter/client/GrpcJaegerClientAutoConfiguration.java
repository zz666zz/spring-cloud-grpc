package com.springcloud.grpc.tracer.jaeger.starter.client;

import com.springcloud.grpc.tracer.jaeger.starter.server.GrpcJaegerServerInterceptor;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
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
public class GrpcJaegerClientAutoConfiguration {

    @Autowired
    private Tracer tracer;

    @Bean
    GrpcJaegerClientInterceptor getGrpcJaegerClientInterceptor() {
        return new GrpcJaegerClientInterceptor(tracer);
    }


}
