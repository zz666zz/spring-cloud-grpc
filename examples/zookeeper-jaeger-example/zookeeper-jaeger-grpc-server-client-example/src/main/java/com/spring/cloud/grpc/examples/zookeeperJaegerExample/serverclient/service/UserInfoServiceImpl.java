package com.spring.cloud.grpc.examples.zookeeperJaegerExample.serverclient.service;


import com.spring.cloud.grpc.examples.common.api.*;
import com.springcloud.grpc.client.core.starter.annotation.GrpcStub;
import com.springcloud.grpc.server.core.starter.service.GrpcService;
import io.grpc.stub.StreamObserver;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@GrpcService
public class UserInfoServiceImpl extends UserInfoServiceGrpc.UserInfoServiceImplBase {

    @GrpcStub("zookeeper-jaeger-grpc-server-example")
    private UserMoneyServiceGrpc.UserMoneyServiceBlockingStub userMoneyServiceBlockingStub;

    @Override
    public void lookUserInfo(LookUserInfoRequest request, StreamObserver<LookUserInfoResponse> responseObserver) {

        int userId = request.getUserId();

        LookMoneyRequest lookMoneyRequest = LookMoneyRequest.newBuilder()
                .setUserId(userId).build();

        LookMoneyResponse lookMoneyResponse = userMoneyServiceBlockingStub.lookMoney(lookMoneyRequest);
        int money = lookMoneyResponse.getMoney();

        LookUserInfoResponse lookUserInfoResponse = LookUserInfoResponse.newBuilder()
                .setUserId(userId)
                .setName("tom")
                .setMoney(money)
                .build();

        responseObserver.onNext(lookUserInfoResponse);
        responseObserver.onCompleted();
    }

}
