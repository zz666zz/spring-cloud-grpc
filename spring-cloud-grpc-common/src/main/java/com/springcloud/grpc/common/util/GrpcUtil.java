package com.springcloud.grpc.common.util;

import io.grpc.MethodDescriptor;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class GrpcUtil {


    public static String extractServiceName(final MethodDescriptor<?, ?> method) {
        return MethodDescriptor.extractFullServiceName(method.getFullMethodName());
    }

    public static String extractMethodName(final MethodDescriptor<?, ?> method) {
        final String fullMethodName = method.getFullMethodName();
        final int index = fullMethodName.lastIndexOf('/');
        if (index == -1) {
            return fullMethodName;
        }
        return fullMethodName.substring(index + 1);
    }


}
