package com.springcloud.grpc.server.core.starter.ServerFactory;

import com.google.common.net.InetAddresses;
import com.springcloud.grpc.common.server.config.GrpcServerProperties;
import com.springcloud.grpc.server.core.starter.service.ServerServiceDefinitionExt;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class NettyGrpcServerFactory {

    private static final Logger log = LoggerFactory.getLogger(NettyGrpcServerFactory.class);
    private GrpcServerProperties grpcServerProperties;
    private final List<ServerServiceDefinitionExt> serverServiceDefinitionExtList = new ArrayList<>();

    public NettyGrpcServerFactory(GrpcServerProperties grpcServerProperties) {
        this.grpcServerProperties = grpcServerProperties;
    }

    protected NettyServerBuilder newServerBuilder() {
        String address = grpcServerProperties.getAddress();
        int port = grpcServerProperties.getPort();
        return "anyLocalAddress".equals(address) ? NettyServerBuilder.forPort(port)
                : NettyServerBuilder.forAddress(new InetSocketAddress(InetAddresses.forString(address), port));
    }

    public void addService(ServerServiceDefinitionExt service) {
        this.serverServiceDefinitionExtList.add(service);
    }

    public Server buildOneServer() {
        NettyServerBuilder builder = this.newServerBuilder();
        this.configure(builder);
        return builder.build();
    }

    protected void configure(NettyServerBuilder builder) {
        this.configureServices(builder);
    }

    protected void configureServices(NettyServerBuilder builder) {
        Iterator var2 = this.serverServiceDefinitionExtList.iterator();
        while(var2.hasNext()) {
            ServerServiceDefinitionExt serverServiceDefinitionExt = (ServerServiceDefinitionExt)var2.next();
            String serviceName = serverServiceDefinitionExt.getServerServiceDefinition().getServiceDescriptor().getName();
            builder.addService(serverServiceDefinitionExt.getServerServiceDefinition());
            log.info("Registered GrpcService: " + serviceName + ", beanName: " + serverServiceDefinitionExt.getBeanName() + ", className: " + serverServiceDefinitionExt.getBeanClazz().getName());
        }

    }

    public String getAddress() {
        return this.grpcServerProperties.getAddress();
    }

    public int getPort() {
        return this.grpcServerProperties.getPort();
    }


}
