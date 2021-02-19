package com.springcloud.grpc.server.core.starter.service;

import com.springcloud.grpc.server.core.starter.interceptor.GlobalGrpcServerInterceptorRegistry;
import io.grpc.BindableService;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class CollectGrpcService implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(CollectGrpcService.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<ServerServiceDefinitionExt> collect() {
        GlobalGrpcServerInterceptorRegistry globalGrpcServerInterceptorRegistry = (GlobalGrpcServerInterceptorRegistry)this.applicationContext.getBean(GlobalGrpcServerInterceptorRegistry.class);
        List<String> beanNames = Arrays.asList(this.applicationContext.getBeanNamesForAnnotation(GrpcService.class));
        List<ServerServiceDefinitionExt> serverServiceDefinitionExtList = new ArrayList<>();
        Iterator iterator = beanNames.iterator();
        while (iterator.hasNext()){
            String beanName = (String) iterator.next();
            BindableService bindableService = this.applicationContext.getBean(beanName, BindableService.class);
            ServerServiceDefinition serverServiceDefinition = bindableService.bindService();
            serverServiceDefinition = ServerInterceptors.interceptForward(serverServiceDefinition, globalGrpcServerInterceptorRegistry.getServerInterceptors());
            ServerServiceDefinitionExt serverServiceDefinitionExt = new ServerServiceDefinitionExt(beanName, bindableService.getClass(), serverServiceDefinition);
            serverServiceDefinitionExtList.add(serverServiceDefinitionExt);
            log.debug("collected GrpcService: " + serverServiceDefinition.getServiceDescriptor().getName() + ", beanName: " + beanName + ", className: " + bindableService.getClass().getName());
        }
        return serverServiceDefinitionExtList;
    }


}
