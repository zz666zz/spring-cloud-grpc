package com.springcloud.grpc.server.core.starter.autoconfigure;

import com.springcloud.grpc.common.server.config.GrpcServerProperties;
import com.springcloud.grpc.server.core.starter.interceptor.AnnotationGlobalGrpcServerInterceptorConfigurer;
import com.springcloud.grpc.server.core.starter.interceptor.GlobalGrpcServerInterceptorRegistry;
import com.springcloud.grpc.server.core.starter.interceptor.SpringContextGlobalGrpcServerInterceptorConfigurer;
import com.springcloud.grpc.server.core.starter.service.CollectGrpcService;
import io.grpc.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@Configuration
@EnableConfigurationProperties(GrpcServerProperties.class)
@ConditionalOnClass({Server.class})
public class GrpcServerAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private GrpcServerProperties grpcServerProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SpringContextGlobalGrpcServerInterceptorConfigurer springContextGlobalGrpcServerInterceptorConfigurer() {
        return new SpringContextGlobalGrpcServerInterceptorConfigurer();
    }

    @Bean
    public AnnotationGlobalGrpcServerInterceptorConfigurer annotationGlobalServerInterceptorConfigurer() {
        return new AnnotationGlobalGrpcServerInterceptorConfigurer();
    }

    @ConditionalOnMissingBean
    @Bean
    public GlobalGrpcServerInterceptorRegistry globalServerInterceptorRegistry() {
        return new GlobalGrpcServerInterceptorRegistry();
    }

    @ConditionalOnMissingBean
    @Bean
    public CollectGrpcService serverServiceDefinitionExtDiscoverer() {
        return new CollectGrpcService();
    }



}
