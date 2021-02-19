package com.springcloud.grpc.server.core.starter.autoconfigure;

import com.springcloud.grpc.common.server.config.GrpcServerProperties;
import com.springcloud.grpc.server.core.starter.ServerFactory.GrpcServerLifecycle;
import com.springcloud.grpc.server.core.starter.ServerFactory.NettyGrpcServerFactory;
import com.springcloud.grpc.server.core.starter.service.CollectGrpcService;
import com.springcloud.grpc.server.core.starter.service.ServerServiceDefinitionExt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Iterator;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class NettyGrpcServerFactoryAutoConfiguration {

    @Bean
    @ConditionalOnClass(name = {"io.netty.channel.Channel", "io.grpc.netty.NettyServerBuilder"})
    public NettyGrpcServerFactory NettyGrpcServerBuilderFactory(GrpcServerProperties properties, CollectGrpcService collectGrpcService) {
        NettyGrpcServerFactory factory = new NettyGrpcServerFactory(properties);
        Iterator var5 = collectGrpcService.collect().iterator();
        while(var5.hasNext()) {
            ServerServiceDefinitionExt service = (ServerServiceDefinitionExt)var5.next();
            factory.addService(service);
        }
        return factory;
    }


    @ConditionalOnBean({NettyGrpcServerFactory.class})
    @Bean
    public GrpcServerLifecycle nettyGrpcServerLifecycle(NettyGrpcServerFactory factory) {
        return new GrpcServerLifecycle(factory);
    }



}
