package com.springcloud.grpc.server.core.starter.interceptor;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface GlobalGrpcServerInterceptor {
}
