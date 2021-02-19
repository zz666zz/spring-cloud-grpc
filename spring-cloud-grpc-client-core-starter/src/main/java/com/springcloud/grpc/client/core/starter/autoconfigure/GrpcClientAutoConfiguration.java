package com.springcloud.grpc.client.core.starter.autoconfigure;

import com.springcloud.grpc.client.core.starter.beanPostProcessor.GrpcStubBeanPostProcessor;
import com.springcloud.grpc.client.core.starter.channelfactory.NettyManagedChannelFactory;
import com.springcloud.grpc.client.core.starter.interceptor.AnnotationGlobalGrpcClientInterceptorConfigurer;
import com.springcloud.grpc.client.core.starter.interceptor.GlobalGrpcClientInterceptorRegistry;
import com.springcloud.grpc.client.core.starter.interceptor.SpringContextGlobalGrpcClientInterceptorConfigurer;
import com.springcloud.grpc.client.core.starter.nameresolver.DiscoveryClientNameResolverProvider;
import com.springcloud.grpc.client.core.starter.nameresolver.StaticNameResolverProvider;
import com.springcloud.grpc.common.clent.config.AllGrpcClientProperties;
import io.grpc.Grpc;
import io.grpc.NameResolverRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@Configuration
@EnableConfigurationProperties(AllGrpcClientProperties.class)
@ConditionalOnClass({Grpc.class})
public class GrpcClientAutoConfiguration {

    @Bean
    static GrpcStubBeanPostProcessor grpcClientBeanPostProcessor(ApplicationContext applicationContext) {
        applicationContext.getBean(StaticNameResolverProvider.class);
        applicationContext.getBean(DiscoveryClientNameResolverProvider.class);
        return new GrpcStubBeanPostProcessor(applicationContext);
    }

    @Bean
    public SpringContextGlobalGrpcClientInterceptorConfigurer springContextGlobalGrpcClientInterceptorConfigurer() {
        return new SpringContextGlobalGrpcClientInterceptorConfigurer();
    }

    @Bean
    public AnnotationGlobalGrpcClientInterceptorConfigurer annotationGlobalGrpcClientInterceptorConfigurer() {
        return new AnnotationGlobalGrpcClientInterceptorConfigurer();
    }


    @Bean
    @ConditionalOnMissingBean
    GlobalGrpcClientInterceptorRegistry globalGrpcClientInterceptorRegistry() {
        return new GlobalGrpcClientInterceptorRegistry();
    }

    @Bean
    @ConditionalOnMissingBean({NettyManagedChannelFactory.class})
    @ConditionalOnClass(name = {"io.netty.channel.Channel", "io.grpc.netty.NettyChannelBuilder"})
    NettyManagedChannelFactory getNettyChannelFactory(AllGrpcClientProperties allGrpcClientProperties, GlobalGrpcClientInterceptorRegistry globalGrpcClientInterceptorRegistry) {
        return new NettyManagedChannelFactory(allGrpcClientProperties, globalGrpcClientInterceptorRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    StaticNameResolverProvider getStaticNameResolverProvider() {
        StaticNameResolverProvider staticNameResolverProvider = new StaticNameResolverProvider();
        NameResolverRegistry nameResolverRegistry = NameResolverRegistry.getDefaultRegistry();
        nameResolverRegistry.register(staticNameResolverProvider);
        return staticNameResolverProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    DiscoveryClientNameResolverProvider getDiscoveryClientNameResolverProvider(DiscoveryClient discoveryClient) {
        DiscoveryClientNameResolverProvider discoveryClientNameResolverProvider = new DiscoveryClientNameResolverProvider(discoveryClient);
        NameResolverRegistry nameResolverRegistry = NameResolverRegistry.getDefaultRegistry();
        nameResolverRegistry.register(discoveryClientNameResolverProvider);
        return discoveryClientNameResolverProvider;
    }



}
