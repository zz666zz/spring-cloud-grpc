package com.springcloud.grpc.metric.micrometer.starter.client;

import com.springcloud.grpc.common.util.GrpcUtil;
import com.springcloud.grpc.metric.micrometer.starter.common.MetricConstants;
import io.grpc.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class GrpcMicrometerClientInterceptor implements ClientInterceptor {

    private MeterRegistry meterRegistry;
    private final Map<MethodDescriptor<?, ?>, GrpcClientMethodMetricSet> allGrpcClientMethodMetricSet = new ConcurrentHashMap<>();

    public GrpcMicrometerClientInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }


    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {

        final GrpcClientMethodMetricSet grpcClientMethodMetricSet = getGrpcClientMethodMetricSet(methodDescriptor);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor,callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {

                Listener<RespT> forwardListener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    private final Timer.Sample timerSample = Timer.start(meterRegistry);
                    @Override
                    public void onClose(final Status status, final Metadata metadata) {
                        Timer timer = grpcClientMethodMetricSet.getGrpcClientMethodCodeTimerFunction().apply(status.getCode());
                        timerSample.stop(timer);
                        super.onClose(status, metadata);
                    }
                    @Override
                    public void onMessage(RespT message) {
                        grpcClientMethodMetricSet.getGrpcClientMethodResponseReceivedCounter().increment();
                        super.onMessage(message);
                    }
                };
                this.delegate().start(forwardListener, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                grpcClientMethodMetricSet.getGrpcClientMethodRequestSentCounter().increment();
                this.delegate().sendMessage(message);
            }

        };
    }

    private GrpcClientMethodMetricSet getGrpcClientMethodMetricSet(final MethodDescriptor<?, ?> method) {
        return this.allGrpcClientMethodMetricSet.computeIfAbsent(method, this::createGrpcClientMethodMetricSet);
    }


    private GrpcClientMethodMetricSet createGrpcClientMethodMetricSet(final MethodDescriptor<?, ?> method) {
        GrpcClientMethodMetricSet grpcClientMetrics = new GrpcClientMethodMetricSet();

        Counter grpcClientMethodRequestSentCounter = Counter.builder(MetricConstants.METRIC_GRPC_CLIENT_METHOD_REQUEST_SENT)
                .description("The total number of grpc client method request sent")
                .baseUnit("messages")
                .tag(MetricConstants.TAG_SERVICE_NAME, GrpcUtil.extractServiceName(method))
                .tag(MetricConstants.TAG_METHOD_NAME, GrpcUtil.extractMethodName(method))
                .tag(MetricConstants.TAG_METHOD_TYPE, method.getType().name())
                .register(meterRegistry);
        grpcClientMetrics.setGrpcClientMethodRequestSentCounter(grpcClientMethodRequestSentCounter);

        Counter grpcClientMethodResponseReceivedCounter = Counter.builder(MetricConstants.METRIC_GRPC_CLIENT_METHOD_RESPONSE_RECEIVED)
                .description("The total number of grpc client method response received")
                .baseUnit("messages")
                .tag(MetricConstants.TAG_SERVICE_NAME, GrpcUtil.extractServiceName(method))
                .tag(MetricConstants.TAG_METHOD_NAME, GrpcUtil.extractMethodName(method))
                .tag(MetricConstants.TAG_METHOD_TYPE, method.getType().name())
                .register(meterRegistry);
        grpcClientMetrics.setGrpcClientMethodResponseReceivedCounter(grpcClientMethodResponseReceivedCounter);

        Function<Status.Code, Timer> methodCodeTimerFunction = createMethodCodeTimerFunction(method);
        grpcClientMetrics.setGrpcClientMethodCodeTimerFunction(methodCodeTimerFunction);

        return grpcClientMetrics;
    }

    Function<Status.Code, Timer> createMethodCodeTimerFunction(MethodDescriptor<?, ?> method) {
        final Map<Status.Code, Timer> cache = new EnumMap<>(Status.Code.class);
        final Function<Status.Code, Timer> creator = code -> Timer.builder(MetricConstants.METRIC_GRPC_CLIENT_METHOD_REQUEST_DURATION)
                .description("The total time taken for the client to complete the call")
                .tag(MetricConstants.TAG_SERVICE_NAME, GrpcUtil.extractServiceName(method))
                .tag(MetricConstants.TAG_METHOD_NAME, GrpcUtil.extractMethodName(method))
                .tag(MetricConstants.TAG_METHOD_TYPE, method.getType().name())
                .tag(MetricConstants.TAG_STATUS_CODE, code.name())
                .register(this.meterRegistry);
        final Function<Status.Code, Timer> cacheResolver = code -> cache.computeIfAbsent(code, creator);
        return cacheResolver;
    }



    private static class GrpcClientMethodMetricSet {

        private Counter grpcClientMethodRequestSentCounter;
        private Counter grpcClientMethodResponseReceivedCounter;
        private Function<Status.Code, Timer> grpcClientMethodCodeTimerFunction;

        private Counter getGrpcClientMethodRequestSentCounter() {
            return grpcClientMethodRequestSentCounter;
        }

        private void setGrpcClientMethodRequestSentCounter(Counter grpcClientMethodRequestSentCounter) {
            this.grpcClientMethodRequestSentCounter = grpcClientMethodRequestSentCounter;
        }

        private Counter getGrpcClientMethodResponseReceivedCounter() {
            return grpcClientMethodResponseReceivedCounter;
        }

        private void setGrpcClientMethodResponseReceivedCounter(Counter grpcClientMethodResponseReceivedCounter) {
            this.grpcClientMethodResponseReceivedCounter = grpcClientMethodResponseReceivedCounter;
        }

        public Function<Status.Code, Timer> getGrpcClientMethodCodeTimerFunction() {
            return grpcClientMethodCodeTimerFunction;
        }

        public void setGrpcClientMethodCodeTimerFunction(Function<Status.Code, Timer> grpcClientMethodCodeTimerFunction) {
            this.grpcClientMethodCodeTimerFunction = grpcClientMethodCodeTimerFunction;
        }
    }


}
