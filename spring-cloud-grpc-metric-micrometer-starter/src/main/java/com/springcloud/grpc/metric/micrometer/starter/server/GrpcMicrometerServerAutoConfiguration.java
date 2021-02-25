package com.springcloud.grpc.metric.micrometer.starter.server;

import com.springcloud.grpc.common.util.GrpcUtil;
import com.springcloud.grpc.metric.micrometer.starter.common.MetricConstants;
import com.springcloud.grpc.metric.micrometer.starter.common.Utils;
import io.grpc.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
@Configuration
public class GrpcMicrometerServerAutoConfiguration {

    @Autowired
    private MeterRegistry meterRegistry;

    @Bean
    GrpcMicrometerServerInterceptor getGrpcMicrometerServerInterceptor(){
        return new GrpcMicrometerServerInterceptor(meterRegistry);
    }

}
