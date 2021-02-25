package com.springcloud.grpc.metric.micrometer.starter.client;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
@Configuration
public class GrpcMicrometerClientAutoConfiguration {

    @Autowired
    private MeterRegistry meterRegistry;

    @Bean
    GrpcMicrometerClientInterceptor getGrpcMicrometerClientInterceptor(){
        return new GrpcMicrometerClientInterceptor(meterRegistry);
    }

}
