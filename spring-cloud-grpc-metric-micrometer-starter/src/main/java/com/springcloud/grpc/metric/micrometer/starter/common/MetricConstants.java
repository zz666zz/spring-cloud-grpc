package com.springcloud.grpc.metric.micrometer.starter.common;

/**
 * @author ZouZhen (shenmedoubuzhidaoa@gmail.com)
 */
public class MetricConstants {

    public static final String METRIC_GRPC_CLIENT_METHOD_REQUEST_SENT = "grpc.client.method.request.sent";

    public static final String METRIC_GRPC_CLIENT_METHOD_RESPONSE_RECEIVED = "grpc.client.method.response.received";

    public static final String METRIC_GRPC_CLIENT_METHOD_REQUEST_DURATION = "grpc.client.method.request.duration";

    public static final String TAG_APP_NAME = "application";

    public static final String TAG_SERVICE_NAME = "service";

    public static final String TAG_METHOD_NAME = "method";

    public static final String TAG_METHOD_TYPE = "methodType";

    public static final String TAG_STATUS_CODE = "statusCode";



}
