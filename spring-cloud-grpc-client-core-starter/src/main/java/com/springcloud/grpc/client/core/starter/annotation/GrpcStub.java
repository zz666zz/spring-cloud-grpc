package com.springcloud.grpc.client.core.starter.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GrpcStub {
    String value();
}
