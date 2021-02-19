package com.springcloud.grpc.tracer.jaeger.starter.common;

import io.opentracing.Span;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public interface ActiveSpanSource {

    /**
     * ActiveSpanSource implementation that always returns
     *  null as the active span
     */
    public static ActiveSpanSource NONE = new ActiveSpanSource() {
        @Override
        public Span getActiveSpan() {
            return null;
        }
    };

    /**
     * ActiveSpanSource implementation that returns the
     *  current span stored in the GRPC context under
     *  {@link OpenTracingContextKey}
     */
    public static ActiveSpanSource GRPC_CONTEXT = new ActiveSpanSource() {
        @Override
        public Span getActiveSpan() {
            return OpenTracingContextKey.activeSpan();
        }
    };

    /**
     * @return the active span
     */
    public Span getActiveSpan();
}
