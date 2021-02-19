package com.springcloud.grpc.server.core.starter.service;

import io.grpc.ServerServiceDefinition;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class ServerServiceDefinitionExt {

    private final String beanName;
    private final Class<?> beanClazz;
    private final ServerServiceDefinition serverServiceDefinition;

    public ServerServiceDefinitionExt(String beanName, Class<?> beanClazz, ServerServiceDefinition serverServiceDefinition) {
        this.beanName = beanName;
        this.beanClazz = beanClazz;
        this.serverServiceDefinition = serverServiceDefinition;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public Class<?> getBeanClazz() {
        return this.beanClazz;
    }

    public ServerServiceDefinition getServerServiceDefinition() {
        return this.serverServiceDefinition;
    }

}
