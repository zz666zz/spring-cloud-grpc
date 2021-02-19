# spring-cloud-grpc
springcloud grpc zookeeper nacos jaeger skywalking promethues elasticseaarch

# Features
* **Combining SpringCloud with grpc** 
* **On demand dependence:** Through the built-in extension mechanism and the external configuration of springboot, the zero coupling between each module is realized


# Components
* **spring-cloud-grpc-dependencies:** Management version
* **spring-cloud-grpc-server-core-starter:** Expose the service through @GrpcService and start the grpc port
* **spring-cloud-grpc-client-core-starter:** Reference services through @GrpcStub
* **spring-cloud-grpc-discover-zookeeper-starter:** Using zookeeper as the registry [Optional]
* **spring-cloud-grpc-discover-nacos-starter:** Using nacos as the registry [Optional]
* **spring-cloud-grpc-tracer-jaeger-starter:** Using Jaeger as the implementation of opentracing [Optional]




