package com.springcloud.grpc.discover.nacos.starter.server.registry;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.springcloud.grpc.common.server.config.GrpcServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@ConditionalOnProperty(name = "spring.cloud.nacos.discovery.register-enabled",havingValue = "true")
@Configuration
@EnableConfigurationProperties(GrpcServerProperties.class)
@ConditionalOnClass({NacosDiscoveryProperties.class, NacosNamingService.class})
public class ServerNacosMetadataRegistryAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ServerNacosMetadataRegistryAutoConfiguration.class);

    @Autowired(required = false)
    private NacosRegistration nacosRegistration;

    @Autowired
    private GrpcServerProperties grpcServerProperties;

    @PostConstruct
    public void init() {
        if (this.nacosRegistration != null) {
            log.info("nacosRegistration is not null, so put server metadata to NacosServer");

            int serverPort = this.grpcServerProperties.getPort();
            if (serverPort > 0){
                this.nacosRegistration.setPort(serverPort);
            }

        }
    }


}
