package com.spring.cloud.grpc.examples.zookeeperExample.client.controller;

import com.spring.cloud.grpc.examples.common.api.LookMoneyRequest;
import com.spring.cloud.grpc.examples.common.api.LookMoneyResponse;
import com.spring.cloud.grpc.examples.common.api.UserMoneyServiceGrpc;
import com.springcloud.grpc.client.core.starter.annotation.GrpcStub;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("userMoney")
public class UserMoneyController {

    @GrpcStub("zookeeper-grpc-server-example")
    private UserMoneyServiceGrpc.UserMoneyServiceBlockingStub userMoneyServiceBlockingStub;


    @GetMapping("lookMoney")
    public int look(@RequestParam("id") Integer id) {
        LookMoneyRequest request = LookMoneyRequest.newBuilder().setUserId(id).build();
        LookMoneyResponse response = null;
        try {
            response = userMoneyServiceBlockingStub.lookMoney(request);
            return response.getMoney();
        }catch (StatusRuntimeException s){
            log.error("error: {}", s.getMessage(), s);
        }
        return 0;
    }









}
