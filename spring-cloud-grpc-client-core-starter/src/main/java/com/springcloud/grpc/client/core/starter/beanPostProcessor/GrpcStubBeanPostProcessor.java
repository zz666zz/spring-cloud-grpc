package com.springcloud.grpc.client.core.starter.beanPostProcessor;

import com.springcloud.grpc.client.core.starter.annotation.GrpcStub;
import com.springcloud.grpc.client.core.starter.channelfactory.NettyManagedChannelFactory;
import io.grpc.Channel;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class GrpcStubBeanPostProcessor implements BeanPostProcessor {

    private ApplicationContext applicationContext;

    private volatile NettyManagedChannelFactory nettyManagedChannelFactory;

    public GrpcStubBeanPostProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class beanClass = bean.getClass();
        Field[] beanFileds = beanClass.getDeclaredFields();
        int beanFiledsNum = beanFileds.length;
        GrpcStub annotation = null;
        for (int i = 0; i < beanFiledsNum; ++i){
            Field field = beanFileds[i];
            annotation = (GrpcStub) AnnotationUtils.findAnnotation(field, GrpcStub.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                AbstractStub stub = this.getExpectedGrpcStub(field, annotation);
                ReflectionUtils.setField(field, bean, stub);
            }
        }
        return bean;
    }

    private AbstractStub getExpectedGrpcStub(Field field, GrpcStub annotation) {
        String remoteServerName = annotation.value();
        Channel channel;
        try {
            channel = this.getChannelFactory().createChannel(remoteServerName);
            if (channel == null) {
                throw new IllegalStateException("channel is null : " + remoteServerName);
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to create channel: " + remoteServerName, e);
        }
        Class injectionType = field.getType();
        AbstractStub<?> stub = this.createStub(injectionType.asSubclass(AbstractStub.class), channel);
        return (AbstractStub) injectionType.cast(stub);
    }

    private NettyManagedChannelFactory getChannelFactory() {
        if (this.nettyManagedChannelFactory == null) {
            NettyManagedChannelFactory factory = (NettyManagedChannelFactory)this.applicationContext.getBean(NettyManagedChannelFactory.class);
            this.nettyManagedChannelFactory = factory;
            return factory;
        } else {
            return this.nettyManagedChannelFactory;
        }
    }

    private <T extends AbstractStub<T>> T createStub(Class<T> stubType, Channel channel) {
        try {
            String methodName = this.deriveStubFactoryMethodName(stubType); /*example: newBlockingStub*/
            Class<?> enclosingClass = stubType.getEnclosingClass();
            Method factoryMethod = enclosingClass.getMethod(methodName, Channel.class);
            return (T) stubType.cast(factoryMethod.invoke((Object)null, channel));
        } catch (Exception e1) {
            try {
                Constructor<T> constructor = stubType.getDeclaredConstructor(Channel.class);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(channel);
            } catch (Exception e2) {
                e1.addSuppressed(e2);
                throw new BeanInstantiationException(stubType, "Failed to create gRPC client", e1);
            }
        }
    }

    private String deriveStubFactoryMethodName(Class<? extends AbstractStub<?>> stubType) {
        if (AbstractAsyncStub.class.isAssignableFrom(stubType)) {
            return "newStub";
        } else if (AbstractBlockingStub.class.isAssignableFrom(stubType)) {
            return "newBlockingStub";
        } else if (AbstractFutureStub.class.isAssignableFrom(stubType)) {
            return "newFutureStub";
        } else {
            throw new IllegalArgumentException("Unsupported stub type");
        }
    }


}
