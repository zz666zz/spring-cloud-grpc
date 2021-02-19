package com.springcloud.grpc.client.core.starter.nameresolver;

import com.google.common.collect.ImmutableList;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class StaticNameResolver extends NameResolver {

    private final String authority;
    private final NameResolver.ResolutionResult result;

    public StaticNameResolver(String authority, List<EquivalentAddressGroup> targets) {
        this.authority = authority;
        this.result = NameResolver.ResolutionResult.newBuilder().setAddresses(targets).build();
    }

    public String getServiceAuthority() {
        return this.authority;
    }

    public void start(NameResolver.Listener2 resolverResultListener) {
        resolverResultListener.onResult(this.result);
    }

    public void refresh() {
    }

    public void shutdown() {
    }


}
