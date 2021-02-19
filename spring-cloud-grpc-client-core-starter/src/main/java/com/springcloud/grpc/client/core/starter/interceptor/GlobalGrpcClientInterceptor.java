package com.springcloud.grpc.client.core.starter.interceptor;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@Component
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalGrpcClientInterceptor {
}
