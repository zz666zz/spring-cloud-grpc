package com.springcloud.grpc.server.core.starter.service;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface GrpcService {
}
