package com.springcloud.grpc.server.core.starter.interceptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class GlobalGrpcServerInterceptorRegistry {
    private static final Logger log = LoggerFactory.getLogger(GlobalGrpcServerInterceptorRegistry.class);
    private final List<ServerInterceptor> serverInterceptorList = new ArrayList<>();
    private ImmutableList<ServerInterceptor> serverInterceptorImmutableList;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        Map<String, GlobalGrpcServerInterceptorConfigurer> map = context.getBeansOfType(GlobalGrpcServerInterceptorConfigurer.class);
        Iterator iterator = map.values().iterator();
        while(iterator.hasNext()) {
            GlobalGrpcServerInterceptorConfigurer globalServerInterceptorConfigurerAdapter = (GlobalGrpcServerInterceptorConfigurer) iterator.next();
            globalServerInterceptorConfigurerAdapter.config(this);
        }
    }

    public GlobalGrpcServerInterceptorRegistry addServerInterceptors(ServerInterceptor interceptor) {
        this.serverInterceptorImmutableList = null;
        this.serverInterceptorList.add(interceptor);
        return this;
    }

    public ImmutableList<ServerInterceptor> getServerInterceptors() {
        if (this.serverInterceptorImmutableList == null) {
            List<ServerInterceptor> temp = Lists.newArrayList(this.serverInterceptorList);
            this.sortInterceptors(temp);
            this.serverInterceptorImmutableList = ImmutableList.copyOf(temp);
        }
        return this.serverInterceptorImmutableList;
    }

    public void sortInterceptors(List<? extends ServerInterceptor> interceptors) {
        interceptors.sort(AnnotationAwareOrderComparator.INSTANCE);
    }

}
