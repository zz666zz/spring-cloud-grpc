package com.springcloud.grpc.common.clent.config;

import io.grpc.netty.NegotiationType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */

public class GrpcClientProperties {

    private NegotiationType negotiationType = NegotiationType.PLAINTEXT;
    private URI address = null;
    private String defaultLoadBalancingPolicy = "round_robin";

    public URI getAddress() {
        return this.address;
    }

    public void setAddress(URI address) {
        this.address = address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : URI.create(address);
    }

    public String getDefaultLoadBalancingPolicy() {
        return this.defaultLoadBalancingPolicy;
    }

    public void setDefaultLoadBalancingPolicy(String defaultLoadBalancingPolicy) {
        this.defaultLoadBalancingPolicy = defaultLoadBalancingPolicy;
    }

    public NegotiationType getNegotiationType() {
        return this.negotiationType;
    }

    public void setNegotiationType(NegotiationType negotiationType) {
        this.negotiationType = negotiationType;
    }

}
