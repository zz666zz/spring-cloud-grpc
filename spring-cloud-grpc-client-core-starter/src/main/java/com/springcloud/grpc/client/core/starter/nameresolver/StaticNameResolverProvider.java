package com.springcloud.grpc.client.core.starter.nameresolver;

import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class StaticNameResolverProvider extends NameResolverProvider {

    private static final Pattern pattern= Pattern.compile(",");

    @Nullable
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {

        if (!"static".equals(targetUri.getScheme())) {
            return null;
        } else {
            String targetAuthority = targetUri.getAuthority();
            String[] hosts = pattern.split(targetAuthority);
            List<EquivalentAddressGroup> targets = new ArrayList(hosts.length);

            for(int i = 0; i < hosts.length; ++i) {
                String host = hosts[i];
                URI uri = URI.create("//" + host);
                int port = uri.getPort();
                if (port == -1) {
                    port = args.getDefaultPort();
                }
                targets.add(new EquivalentAddressGroup(new InetSocketAddress(uri.getHost(), port)));
            }
            if (targets.isEmpty()) {
                throw new IllegalArgumentException("targets is null : " + targetAuthority);
            } else {
                return new StaticNameResolver(targetAuthority, targets);
            }
        }
    }

    @Override
    public String getDefaultScheme() {
        return "static";
    }


    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 500;
    }


}
