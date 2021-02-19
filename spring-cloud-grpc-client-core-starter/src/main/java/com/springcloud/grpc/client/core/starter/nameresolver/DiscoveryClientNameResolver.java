package com.springcloud.grpc.client.core.starter.nameresolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.grpc.*;
import io.grpc.internal.SharedResourceHolder;
import io.grpc.internal.SharedResourceHolder.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class DiscoveryClientNameResolver extends NameResolver {
    private static final Logger log = LoggerFactory.getLogger(DiscoveryClientNameResolver.class);
    private static final List<ServiceInstance> KEEP_PREVIOUS = null;
    private final DiscoveryClient discoveryClient;
    private final String serviceName;
    private List<ServiceInstance> cachedServiceInstanceList = Lists.newArrayList();
    private final SynchronizationContext syncContext;
    private final Runnable externalCleanerRunnable;
    private final Resource<Executor> executorResource;
    private final boolean usingExecutorResource;
    private Listener2 resolverResultListener;
    private Executor executor;
    private boolean resolving;


    public DiscoveryClientNameResolver(String serviceName, DiscoveryClient discoveryClient, Args args, Resource<Executor> executorResource, Runnable externalCleanerRunnable) {
        this.executor = args.getOffloadExecutor();
        this.serviceName = serviceName;
        this.discoveryClient = discoveryClient;
        this.syncContext = args.getSynchronizationContext();
        this.externalCleanerRunnable = externalCleanerRunnable;
        this.executorResource = executorResource;
        this.usingExecutorResource = this.executor == null;
    }

    @Override
    public String getServiceAuthority() {
        return this.serviceName;
    }

    @Override
    public void shutdown() {
        log.debug("start shutdown NameResolver for {}", serviceName);
        this.resolverResultListener = null;
        if (this.executor != null && this.usingExecutorResource) {
            this.executor = (Executor)SharedResourceHolder.release(this.executorResource, this.executor);
        }
        this.cachedServiceInstanceList = Lists.newArrayList();
        if (this.externalCleanerRunnable != null) {
            this.externalCleanerRunnable.run();
        }
    }

    @Override
    public void start(Listener2 resolverResultListener) {
        log.debug("【start()】for {}", this.serviceName);
        Preconditions.checkState(this.resolverResultListener == null, "this.listener not null");
        this.resolverResultListener = resolverResultListener;
        if (this.usingExecutorResource) {
            this.executor = (Executor)SharedResourceHolder.get(this.executorResource);
        }
        this.resolve();
    }

    @Override
    public void refresh() {
        log.debug("【refresh()】for {}", this.serviceName);
        Preconditions.checkState(this.resolverResultListener != null, "this.listener is null");
        this.resolve();
    }

    public void refreshFromExternal() {
        log.debug("【refreshFromExternal()】for {}", this.serviceName);
        this.syncContext.execute(() -> {
            if (this.resolverResultListener != null) {
                this.resolve();
            }
        });
    }

    private void resolve() {
        log.debug("******************** start resolve() for {}", this.serviceName);
        if (!this.resolving) {
            this.resolving = true;
            this.executor.execute(new DiscoveryClientNameResolver.ResolveRunnable(this.resolverResultListener, this.cachedServiceInstanceList));
        }
    }

    private final class ResolveRunnable implements Runnable {
        private final Listener2 resolverResultListener;
        private final List<ServiceInstance> cachedServiceInstanceList;

        ResolveRunnable(Listener2 resolverResultListener, List<ServiceInstance> cachedServiceInstanceList) {
            this.resolverResultListener = (Listener2) Objects.requireNonNull(resolverResultListener, "listener");
            this.cachedServiceInstanceList = (List) Objects.requireNonNull(cachedServiceInstanceList, "cachedServiceInstanceList");
        }

        public void run() {
            AtomicReference resultContainer = new AtomicReference();
            try {
                resultContainer.set(this.resolveInternal());
            } catch (Exception var6) {
                this.resolverResultListener.onError(Status.UNAVAILABLE.withCause(var6).withDescription("Failed to update server list for " + DiscoveryClientNameResolver.this.serviceName));
                resultContainer.set(Lists.newArrayList());
            } finally {
                DiscoveryClientNameResolver.this.syncContext.execute(() -> {
                    DiscoveryClientNameResolver.this.resolving = false;
                    List<ServiceInstance> result = (List) resultContainer.get();
                    if (result != DiscoveryClientNameResolver.KEEP_PREVIOUS && DiscoveryClientNameResolver.this.resolverResultListener != null) {
                        DiscoveryClientNameResolver.this.cachedServiceInstanceList = result;
                    }

                });
            }
        }

        private List<ServiceInstance> resolveInternal() {
            String serviceName = DiscoveryClientNameResolver.this.serviceName;
            DiscoveryClientNameResolver.log.debug("@@@@@ start get registryCurrentServiceInstanceList from DiscoveryClient for {}", serviceName);
            List<ServiceInstance> registryCurrentServiceInstanceList = DiscoveryClientNameResolver.this.discoveryClient.getInstances(serviceName);
            DiscoveryClientNameResolver.log.debug("@@@@@ End got {} registryCurrentServiceInstance from DiscoveryClient for {}", registryCurrentServiceInstanceList.size(), serviceName);
            if (CollectionUtils.isEmpty(registryCurrentServiceInstanceList)) {
                DiscoveryClientNameResolver.log.error("No registryCurrentServiceInstance found for {}", serviceName);
                this.resolverResultListener.onError(Status.UNAVAILABLE.withDescription("No registryCurrentServiceInstance found for " + serviceName));
                return new ArrayList<>();
            } else if (!this.needUpdateCachedInstanceList(registryCurrentServiceInstanceList)) {
                DiscoveryClientNameResolver.log.debug("not needUpdateCachedInstanceList for {}", serviceName);
                return DiscoveryClientNameResolver.KEEP_PREVIOUS;
            } else {
                DiscoveryClientNameResolver.log.debug("needUpdateCachedInstanceList for {}", serviceName);
                DiscoveryClientNameResolver.log.debug("##### start update this.cachedInstanceList for {}", serviceName);
                List<EquivalentAddressGroup> targets = Lists.newArrayList();
                Iterator registryCurrentServiceInstanceListIterator = registryCurrentServiceInstanceList.iterator();

                while (registryCurrentServiceInstanceListIterator.hasNext()) {
                    ServiceInstance registryCurrentServiceInstance = (ServiceInstance) registryCurrentServiceInstanceListIterator.next();
                    int registryCurrentServiceInstancePort = registryCurrentServiceInstance.getPort();
                    DiscoveryClientNameResolver.log.debug("Found gRPC server {}:{} for {}", new Object[]{registryCurrentServiceInstance.getHost(), registryCurrentServiceInstancePort, serviceName});
                    targets.add(new EquivalentAddressGroup(new InetSocketAddress(registryCurrentServiceInstance.getHost(), registryCurrentServiceInstancePort), Attributes.EMPTY));
                }

                if (targets.isEmpty()) {
                    DiscoveryClientNameResolver.log.error("None of the servers for {} specified a gRPC port", serviceName);
                    this.resolverResultListener.onError(Status.UNAVAILABLE.withDescription("None of the servers for " + serviceName + " specified a gRPC port"));
                    return Lists.newArrayList();
                } else {
                    this.resolverResultListener.onResult(ResolutionResult.newBuilder().setAddresses(targets).build());
                    DiscoveryClientNameResolver.log.info("##### End updating instance list for {}", serviceName);
                    return registryCurrentServiceInstanceList;
                }
            }
        }

        private boolean needUpdateCachedInstanceList(List<ServiceInstance> registryCurrentServiceInstanceList) {
            if (this.cachedServiceInstanceList.size() != registryCurrentServiceInstanceList.size()) {
                return true;
            } else {
                Iterator cachedInstanceListIterator = this.cachedServiceInstanceList.iterator();
                boolean isSame;
                do {
                    if (!cachedInstanceListIterator.hasNext()) {
                        return false;
                    }
                    ServiceInstance cachedServiceInstance = (ServiceInstance)cachedInstanceListIterator.next();
                    int cachedServiceInstancePort = cachedServiceInstance.getPort();
                    isSame = false;
                    Iterator registryCurrentServiceInstanceIterator = registryCurrentServiceInstanceList.iterator();
                    while(registryCurrentServiceInstanceIterator.hasNext()) {
                        ServiceInstance registryCurrentServiceInstance = (ServiceInstance)registryCurrentServiceInstanceIterator.next();
                        int registryCurrentServiceInstancePort = registryCurrentServiceInstance.getPort();
                        if (registryCurrentServiceInstance.getHost().equals(cachedServiceInstance.getHost()) && cachedServiceInstancePort == registryCurrentServiceInstancePort) {
                            isSame = true;
                            break;
                        }
                    }
                } while(isSame);
                return true;
            }
        }



    }






}
