package com.springcloud.grpc.metric.micrometer.starter.common;

import com.springcloud.grpc.common.util.GrpcUtil;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class Utils {


    static public Function<Status.Code, Timer> createMethodCodeTimerFunction(MethodDescriptor<?, ?> method, MeterRegistry meterRegistry, String metricName) {
        final Map<Status.Code, Timer> cache = new EnumMap<>(Status.Code.class);
        final Function<Status.Code, Timer> creator = code -> Timer.builder(metricName)
                .description("The total time taken for the client to complete the call")
                .tag(MetricConstants.TAG_SERVICE_NAME, GrpcUtil.extractServiceName(method))
                .tag(MetricConstants.TAG_METHOD_NAME, GrpcUtil.extractMethodName(method))
                .tag(MetricConstants.TAG_METHOD_TYPE, method.getType().name())
                .tag(MetricConstants.TAG_STATUS_CODE, code.name())
                .register(meterRegistry);
        final Function<Status.Code, Timer> cacheResolver = code -> cache.computeIfAbsent(code, creator);
        return cacheResolver;
    }

}
