package com.springcloud.grpc.client.core.starter.interceptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class GlobalGrpcClientInterceptorRegistry {

    private final List<ClientInterceptor> clientInterceptorList = Lists.newArrayList();
    private ImmutableList<ClientInterceptor> clientInterceptorImmutableList;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        Map<String, GlobalGrpcClientInterceptorConfigurer> map = context.getBeansOfType(GlobalGrpcClientInterceptorConfigurer.class);
        Iterator iterator = map.values().iterator();
        while(iterator.hasNext()) {
            GlobalGrpcClientInterceptorConfigurer globalServerInterceptorConfigurerAdapter = (GlobalGrpcClientInterceptorConfigurer) iterator.next();
            globalServerInterceptorConfigurerAdapter.config(this);
        }
    }


    public GlobalGrpcClientInterceptorRegistry addClientInterceptors(ClientInterceptor interceptor) {
        this.clientInterceptorImmutableList = null;
        this.clientInterceptorList.add(interceptor);
        return this;
    }

    public ImmutableList<ClientInterceptor> getClientInterceptors() {
        if (this.clientInterceptorImmutableList == null) {
            List<ClientInterceptor> temp = Lists.newArrayList(this.clientInterceptorList);
            this.sortInterceptors(temp);
            this.clientInterceptorImmutableList = ImmutableList.copyOf(temp);
        }
        return this.clientInterceptorImmutableList;
    }

    public void sortInterceptors(List<? extends ClientInterceptor> interceptors) {
        interceptors.sort(AnnotationAwareOrderComparator.INSTANCE);
    }

}
