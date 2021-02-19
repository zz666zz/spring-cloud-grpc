package com.springcloud.grpc.discover.zookeeper.starter.server.registry;

import com.springcloud.grpc.common.server.config.GrpcServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
@ConditionalOnProperty(name = "spring.cloud.zookeeper.discovery.register",havingValue = "true")
@Configuration
@EnableConfigurationProperties(GrpcServerProperties.class)
@ConditionalOnClass({ZookeeperRegistration.class})
public class ServerZookeeperMetadataRegistryAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ServerZookeeperMetadataRegistryAutoConfiguration.class);

    @Autowired(required = false)
    private ZookeeperRegistration zookeeperRegistration;

    @Autowired
    private GrpcServerProperties grpcServerProperties;

    @PostConstruct
    public void init() {
        if (this.zookeeperRegistration != null) {
            log.info("zookeeperRegistration is not null, so put server metadata to ZookeeperServer");

            int serverPort = this.grpcServerProperties.getPort();
            if (serverPort > 0){
                zookeeperRegistration.setPort(serverPort);
            }


        }
    }


}
