package com.springcloud.grpc.common.clent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@Component
@ConfigurationProperties("cloud.grpc")
public class AllGrpcClientProperties {

    private Map<String, GrpcClientProperties> clients = new ConcurrentHashMap();


    public void setClients(Map<String, GrpcClientProperties> clients) {
        this.clients = clients;
    }

    public Map<String, GrpcClientProperties> getClients() {
        return clients;
    }

    public GrpcClientProperties getGrpcClientProperties(String serviceName) {
        return (GrpcClientProperties)this.clients.computeIfAbsent(serviceName, (key) -> {
            return new GrpcClientProperties();
        });
    }



}
