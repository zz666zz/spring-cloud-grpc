package com.springcloud.grpc.client.core.starter.channelfactory;

import com.google.common.collect.Lists;
import com.springcloud.grpc.client.core.starter.interceptor.GlobalGrpcClientInterceptorRegistry;
import com.springcloud.grpc.common.clent.config.GrpcClientProperties;
import com.springcloud.grpc.common.clent.config.AllGrpcClientProperties;
import io.grpc.*;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class NettyManagedChannelFactory implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(NettyManagedChannelFactory.class);
    private final AllGrpcClientProperties allGrpcClientProperties;
    private final GlobalGrpcClientInterceptorRegistry globalGrpcClientInterceptorRegistry;
    private volatile boolean shutdown = false;
    private final Map<String, ManagedChannel> channels = new ConcurrentHashMap();
    private final Map<String, ConnectivityState> channelStates = new ConcurrentHashMap();


    public NettyManagedChannelFactory(AllGrpcClientProperties allGrpcClientProperties, GlobalGrpcClientInterceptorRegistry globalGrpcClientInterceptorRegistry) {
        this.allGrpcClientProperties = allGrpcClientProperties;
        this.globalGrpcClientInterceptorRegistry = globalGrpcClientInterceptorRegistry;
    }

    private NettyChannelBuilder newChannelBuilder(String name) {
        GrpcClientProperties properties = this.getPropertiesFor(name);
        URI address = properties.getAddress();
        if (address == null) {
            address = URI.create(name);
        }
        return (NettyChannelBuilder)NettyChannelBuilder.forTarget(address.toString())
                .defaultLoadBalancingPolicy(properties.getDefaultLoadBalancingPolicy());
    }

    private final GrpcClientProperties getPropertiesFor(String name) {
        return this.allGrpcClientProperties.getGrpcClientProperties(name);
    }

    public Channel createChannel(String remoteServerName) {
        Channel channel;
        synchronized(this) {
            if (this.shutdown) {
                throw new IllegalStateException("NettyChannelFactory is closed!");
            }
            channel = (Channel)this.channels.computeIfAbsent(remoteServerName, this::newManagedChannel);
        }
        List<ClientInterceptor> interceptors = Lists.newArrayList(this.globalGrpcClientInterceptorRegistry.getClientInterceptors());
        return ClientInterceptors.interceptForward(channel, interceptors);
    }

    private ManagedChannel newManagedChannel(String remoteServerName) {
        NettyChannelBuilder builder = this.newChannelBuilder(remoteServerName);
        this.configure(builder, remoteServerName);
        ManagedChannel channel = builder.build();
        this.watchConnectivityState(remoteServerName, channel);
        return channel;
    }

    private void configure(NettyChannelBuilder builder, String remoteServerName) {
        this.configureSecurity(builder, remoteServerName);
    }

    private void configureSecurity(NettyChannelBuilder builder, String remoteServerName) {
        GrpcClientProperties properties = this.getPropertiesFor(remoteServerName);
        NegotiationType negotiationType = properties.getNegotiationType();
        builder.negotiationType(negotiationType);
    }


    private void watchConnectivityState(String name, ManagedChannel channel) {
        ConnectivityState state = channel.getState(false);
        this.channelStates.put(name, state);
        if (state != ConnectivityState.SHUTDOWN) {
            channel.notifyWhenStateChanged(state, () -> {
                this.watchConnectivityState(name, channel);
            });
        }

    }


    @Override
    @PreDestroy
    public synchronized void close() {
        if (this.shutdown) {
            return;
        }
        this.shutdown = true;
        for (final ManagedChannel channel : this.channels.values()) {
            channel.shutdown();
        }
        try {
            final long waitLimit = System.currentTimeMillis() + 60000;
            for (final ManagedChannel channel : this.channels.values()) {
                int i = 0;
                do {
                    log.debug("Awaiting channel shutdown: {} ({}s)", channel, i++);
                } while (System.currentTimeMillis() < waitLimit && !channel.awaitTermination(1, TimeUnit.SECONDS));
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            log.debug("We got interrupted - Speeding up shutdown process");
        } finally {
            for (final ManagedChannel channel : this.channels.values()) {
                if (!channel.isTerminated()) {
                    log.debug("Channel not terminated yet - force shutdown now: {} ", channel);
                    channel.shutdownNow();
                }
            }
        }
        this.channels.clear();
        this.channelStates.clear();
        log.debug("NettyManagedChannelFactory closed {} ", this.channels.size());
    }


}
