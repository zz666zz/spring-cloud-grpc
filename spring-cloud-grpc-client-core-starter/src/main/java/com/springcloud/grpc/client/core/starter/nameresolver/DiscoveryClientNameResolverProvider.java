package com.springcloud.grpc.client.core.starter.nameresolver;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.internal.GrpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class DiscoveryClientNameResolverProvider extends NameResolverProvider {
    private static final Logger log = LoggerFactory.getLogger(DiscoveryClientNameResolverProvider.class);
    private final DiscoveryClient discoveryClient;
    private final Set<DiscoveryClientNameResolver> discoveryClientNameResolvers = ConcurrentHashMap.newKeySet();
    private final HeartbeatMonitor monitor = new HeartbeatMonitor();



    public DiscoveryClientNameResolverProvider(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 600;
    }

    @Override
    public String getDefaultScheme() {
        return "discovery";
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (!"discovery".equals(targetUri.getScheme())) {
            return null;
        } else {
            String serviceName = targetUri.getPath();
            if (serviceName != null && serviceName.length() > 1 && serviceName.startsWith("/")) {
                AtomicReference<DiscoveryClientNameResolver> reference = new AtomicReference();
                String newServiceName = serviceName.substring(1);
                DiscoveryClientNameResolver discoveryClientNameResolver = new DiscoveryClientNameResolver(newServiceName, this.discoveryClient, args, GrpcUtil.SHARED_CHANNEL_EXECUTOR, () -> {
                    this.discoveryClientNameResolvers.remove(reference.get());
                });
                reference.set(discoveryClientNameResolver);
                this.discoveryClientNameResolvers.add(discoveryClientNameResolver);
                return discoveryClientNameResolver;
            } else {
                throw new IllegalArgumentException("Error target uri; expected: 'discovery:[//]/<service-name>'; but now is '" + targetUri.toString() + "'");
            }
        }
    }

    @EventListener({HeartbeatEvent.class})
    public void heartbeat(HeartbeatEvent event) {
        if (this.monitor.update(event.getValue())) {
            Iterator resolversIterator = this.discoveryClientNameResolvers.iterator();
            while(resolversIterator.hasNext()) {
                DiscoveryClientNameResolver discoveryClientNameResolver = (DiscoveryClientNameResolver)resolversIterator.next();
                discoveryClientNameResolver.refreshFromExternal();
            }
        }
    }

    @PreDestroy
    public void destroy() {
        log.warn("destroy: " + DiscoveryClientNameResolverProvider.this.toString());
        this.discoveryClientNameResolvers.clear();
    }





}
