package com.springcloud.grpc.common.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

/**
 * @author zouzhen
 */
@Component
@ConfigurationProperties("cloud.grpc.server")
public class GrpcServerProperties {

    private String address = "anyLocalAddress";
    private int port = 13000;

    public int getPort() {
        if (this.port == 0) {
            this.port = SocketUtils.findAvailableTcpPort();
        }
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
