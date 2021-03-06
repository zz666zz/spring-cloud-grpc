package com.spring.cloud.grpc.examples.prometheus.client.controller;

import com.spring.cloud.grpc.examples.common.api.LookMoneyRequest;
import com.spring.cloud.grpc.examples.common.api.LookMoneyResponse;
import com.spring.cloud.grpc.examples.common.api.UserMoneyServiceGrpc;
import com.springcloud.grpc.client.core.starter.annotation.GrpcStub;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserMoneyController {

    @GrpcStub("local-grpc-server-example")
    private UserMoneyServiceGrpc.UserMoneyServiceBlockingStub userMoneyServiceBlockingStub;


    @GetMapping("userMoney/lookMoney")
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


    @GetMapping("userMoney/lookMoney2")
    public int look2(@RequestParam("id") Integer id) {
        LookMoneyRequest request = LookMoneyRequest.newBuilder().setUserId(id).build();
        LookMoneyResponse response = null;
        try {
            response = userMoneyServiceBlockingStub.lookMoney2(request);
            return response.getMoney();
        }catch (StatusRuntimeException s){
            log.error("error: {}", s.getMessage(), s);
        }
        return 0;
    }









}
