package com.spring.cloud.grpc.examples.zookeeperJaegerExample.client.controller;

import com.spring.cloud.grpc.examples.common.api.*;
import com.springcloud.grpc.client.core.starter.annotation.GrpcStub;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("userMoney")
public class UserMoneyController {


    @GrpcStub("zookeeper-jaeger-grpc-server-client-example")
    private UserInfoServiceGrpc.UserInfoServiceBlockingStub userInfoServiceBlockingStub;

    @GetMapping("lookMoney")
    public int look(@RequestParam("id") Integer id) {
        LookUserInfoRequest request = LookUserInfoRequest.newBuilder().setUserId(id).build();
        LookUserInfoResponse response = null;
        try {
            response = userInfoServiceBlockingStub.lookUserInfo(request);
            return response.getMoney();
        }catch (StatusRuntimeException s){
            log.error("error: {}", s.getMessage(), s);
        }
        return 0;
    }









}
