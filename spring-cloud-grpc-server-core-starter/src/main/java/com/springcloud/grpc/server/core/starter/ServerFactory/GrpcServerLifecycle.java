package com.springcloud.grpc.server.core.starter.ServerFactory;

import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:shenmedoubuzhidaoa@gmail.com">ZouZhen</a>
 */
public class GrpcServerLifecycle implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(GrpcServerLifecycle.class);
    private volatile Server server;
    private final NettyGrpcServerFactory factory;
    private static AtomicInteger serverCounter = new AtomicInteger(0);

    public GrpcServerLifecycle(NettyGrpcServerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void start() {
        synchronized (GrpcServerLifecycle.class){
            this.createAndStartGrpcServer();
        }
    }

    @Override
    public void stop() {
        synchronized (GrpcServerLifecycle.class) {
            Server tempServer = this.server;
            if (tempServer != null) {
                tempServer.shutdown();
                this.server = null;
                log.info("gRPC server shutdown.");
            }
        }
    }

    @Override
    public boolean isRunning() {
        return this.server != null && !this.server.isShutdown();
    }

    private void createAndStartGrpcServer() {
        if (this.server == null) {
            this.server = this.factory.buildOneServer();
            try {
                this.server.start();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed to start grpc server", e);
            }
            log.info("grpc Server has started, listening on address: " + this.factory.getAddress() + ", port: " + this.factory.getPort());
            Thread awaitTerminationThread = new Thread(() -> {
                try {
                    this.server.awaitTermination();
                } catch (InterruptedException var2) {
                    log.warn("grpc ServerImpl is stop, listening on address: " + this.factory.getAddress() + ", port: " + this.factory.getPort());
                    Thread.currentThread().interrupt();
                }
            }, "grpc-server-awaitTermination-" + serverCounter.incrementAndGet());
            awaitTerminationThread.setDaemon(false);
            awaitTerminationThread.start();
        }

    }


}
