package com.springcloud.grpc.metric.micrometer.starter.server;

import com.springcloud.grpc.common.util.GrpcUtil;
import com.springcloud.grpc.metric.micrometer.starter.common.MetricConstants;
import com.springcloud.grpc.metric.micrometer.starter.common.Utils;
import io.grpc.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class GrpcMicrometerServerInterceptor implements ServerInterceptor {

    private MeterRegistry meterRegistry;
    private final Map<MethodDescriptor<?, ?>, GrpcServerMethodMetricSet> allGrpcServerMethodMetricSet = new ConcurrentHashMap<>();

    public GrpcMicrometerServerInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata metadata, ServerCallHandler<ReqT, RespT> nextServerCallHandler) {
        GrpcServerMethodMetricSet grpcServerMethodMetricSet = getGrpcServerMethodMetricSet(call.getMethodDescriptor());
        ServerCall<ReqT, RespT> serverCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            private final Timer.Sample timerSample = Timer.start(meterRegistry);
            @Override
            public void sendMessage(RespT message) {
                grpcServerMethodMetricSet.getGrpcServerMethodResponseSentCounter().increment();
                super.sendMessage(message);
            }
            @Override
            public void close(Status status, Metadata trailers) {
                Timer timer = grpcServerMethodMetricSet.getGrpcServerMethodCodeTimerFunction().apply(status.getCode());
                this.timerSample.stop(timer);
                this.delegate().close(status, trailers);
            }
        };
        ServerCall.Listener<ReqT> nextListener = nextServerCallHandler.startCall(serverCall, metadata);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(nextListener) {
            public void onMessage(ReqT message) {
                grpcServerMethodMetricSet.getGrpcServerMethodRequestReceivedCounter().increment();
                super.onMessage(message);
            }
        };
    }

    private GrpcServerMethodMetricSet getGrpcServerMethodMetricSet(final MethodDescriptor<?, ?> method) {
        return this.allGrpcServerMethodMetricSet.computeIfAbsent(method, this::createGrpcServerMethodMetricSet);
    }

    private GrpcServerMethodMetricSet createGrpcServerMethodMetricSet(final MethodDescriptor<?, ?> method) {
        GrpcServerMethodMetricSet grpcServerMetrics = new GrpcServerMethodMetricSet();
        Counter grpcServerMethodRequestReceivedCounter = Counter.builder(MetricConstants.METRIC_GRPC_SERVER_METHOD_REQUEST_RECEIVED)
                .description("The total number of grpc server method request received")
                .baseUnit("messages")
                .tag(MetricConstants.TAG_SERVICE_NAME, GrpcUtil.extractServiceName(method))
                .tag(MetricConstants.TAG_METHOD_NAME, GrpcUtil.extractMethodName(method))
                .tag(MetricConstants.TAG_METHOD_TYPE, method.getType().name())
                .register(meterRegistry);
        grpcServerMetrics.setGrpcServerMethodRequestReceivedCounter(grpcServerMethodRequestReceivedCounter);

        Counter grpcServerMethodResponseSentCounter = Counter.builder(MetricConstants.METRIC_GRPC_SERVER_METHOD_RESPONSE_SENT)
                .description("The total number of grpc server method response sent")
                .baseUnit("messages")
                .tag(MetricConstants.TAG_SERVICE_NAME, GrpcUtil.extractServiceName(method))
                .tag(MetricConstants.TAG_METHOD_NAME, GrpcUtil.extractMethodName(method))
                .tag(MetricConstants.TAG_METHOD_TYPE, method.getType().name())
                .register(meterRegistry);
        grpcServerMetrics.setGrpcServerMethodResponseSentCounter(grpcServerMethodResponseSentCounter);

        Function<Status.Code, Timer> methodCodeTimerFunction = Utils.createMethodCodeTimerFunction(method, this.meterRegistry, MetricConstants.METRIC_GRPC_SERVER_METHOD_PROCESS_REQUEST_DURATION);
        grpcServerMetrics.setGrpcServerMethodCodeTimerFunction(methodCodeTimerFunction);

        return grpcServerMetrics;
    }


    private static class GrpcServerMethodMetricSet {

        private Counter grpcServerMethodRequestReceivedCounter;
        private Counter grpcServerMethodResponseSentCounter;
        private Function<Status.Code, Timer> grpcServerMethodCodeTimerFunction;

        public Counter getGrpcServerMethodRequestReceivedCounter() {
            return grpcServerMethodRequestReceivedCounter;
        }

        public void setGrpcServerMethodRequestReceivedCounter(Counter grpcServerMethodRequestReceivedCounter) {
            this.grpcServerMethodRequestReceivedCounter = grpcServerMethodRequestReceivedCounter;
        }

        public Counter getGrpcServerMethodResponseSentCounter() {
            return grpcServerMethodResponseSentCounter;
        }

        public void setGrpcServerMethodResponseSentCounter(Counter grpcServerMethodResponseSentCounter) {
            this.grpcServerMethodResponseSentCounter = grpcServerMethodResponseSentCounter;
        }

        public Function<Status.Code, Timer> getGrpcServerMethodCodeTimerFunction() {
            return grpcServerMethodCodeTimerFunction;
        }

        public void setGrpcServerMethodCodeTimerFunction(Function<Status.Code, Timer> grpcServerMethodCodeTimerFunction) {
            this.grpcServerMethodCodeTimerFunction = grpcServerMethodCodeTimerFunction;
        }
    }

}
