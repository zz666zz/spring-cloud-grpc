package com.springcloud.grpc.tracer.jaeger.starter.common;

import io.grpc.Context;
import io.opentracing.Span;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class OpenTracingContextKey {

    public static final String KEY_NAME = "io.opentracing.active-span";
    private static final Context.Key<Span> key = Context.key(KEY_NAME);

    /**
     * @return the active span for the current request
     */
    public static Span activeSpan() {
        return key.get();
    }

    /**
     * @return the OpenTracing context key
     */
    public static Context.Key<Span> getKey() {
        return key;
    }
}
