package com.springcloud.grpc.server.core.starter.interceptor;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public interface GlobalGrpcServerInterceptorConfigurer {
    void config(GlobalGrpcServerInterceptorRegistry registry);
}
