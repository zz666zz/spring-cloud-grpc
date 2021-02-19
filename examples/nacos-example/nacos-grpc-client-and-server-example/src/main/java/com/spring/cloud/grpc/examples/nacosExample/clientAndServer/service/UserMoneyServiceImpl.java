package com.spring.cloud.grpc.examples.nacosExample.clientAndServer.service;


import com.spring.cloud.grpc.examples.common.api.LookMoneyRequest;
import com.spring.cloud.grpc.examples.common.api.LookMoneyResponse;
import com.spring.cloud.grpc.examples.common.api.UserMoneyServiceGrpc;
import com.springcloud.grpc.server.core.starter.service.GrpcService;
import io.grpc.stub.StreamObserver;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@GrpcService
public class UserMoneyServiceImpl extends UserMoneyServiceGrpc.UserMoneyServiceImplBase {


    @Override
    public void lookMoney(LookMoneyRequest request, StreamObserver<LookMoneyResponse> responseObserver) {
        int userId = request.getUserId();
        LookMoneyResponse.Builder responseBuilder = LookMoneyResponse.newBuilder();
        responseBuilder.setUserId(userId);
        responseBuilder.setMoney(13005);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
