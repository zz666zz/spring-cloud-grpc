package com.springcloud.grpc.client.core.starter.interceptor;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public interface GlobalGrpcClientInterceptorConfigurer {
    void config(GlobalGrpcClientInterceptorRegistry registry);
}
