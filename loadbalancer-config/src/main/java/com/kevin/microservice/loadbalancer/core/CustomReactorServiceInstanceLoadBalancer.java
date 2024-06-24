package com.kevin.microservice.loadbalancer.core;

import com.kevin.microservice.BizConstant;
import com.kevin.microservice.RequestContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CustomReactorServiceInstanceLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(RoundRobinLoadBalancer.class);

    final AtomicInteger position;

    final String serviceId;

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     */
    public CustomReactorServiceInstanceLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                  String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     * @param seedPosition Round Robin element position marker
     */
    public CustomReactorServiceInstanceLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                  String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }

    @SuppressWarnings("rawtypes")
    @Override
    // see original
    // https://github.com/Netflix/ocelli/blob/master/ocelli-core/
    // src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, request));
    }

    @SuppressWarnings("rawtypes")
    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances,
                                                              Request request) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, request);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    @SuppressWarnings("rawtypes")
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        log.info("thread: " + Thread.currentThread().getName());

        RequestDataContext dataContext = (RequestDataContext) request.getContext();
        HttpHeaders headers = dataContext.getClientRequest().getHeaders();

        String envTag = "";
        if (headers.containsKey(BizConstant.ENV_HEADER)) {
            envTag = headers.get(BizConstant.ENV_HEADER).get(0);
        }

        // 总社服务实例列表，zs标记或者没有任何标记，视为总社服务实例
        List<ServiceInstance> zsInstances = instances.stream()
                .filter(instance -> !instance.getMetadata().containsKey(BizConstant.ENV_HEADER)
                        || (instance.getMetadata().containsKey(BizConstant.ENV_HEADER) && instance.getMetadata().get(BizConstant.ENV_HEADER).equals(BizConstant.ZS_ENV_VALUE)))
                .collect(Collectors.toList());

        // 东坝服务实例列表，db标记，视为东坝服务实例
        List<ServiceInstance> dbInstances = instances.stream()
                .filter(instance -> instance.getMetadata().containsKey(BizConstant.ENV_HEADER) && instance.getMetadata().get(BizConstant.ENV_HEADER).equals(BizConstant.DB_ENV_VALUE))
                .collect(Collectors.toList());

        if (envTag.equals(BizConstant.ZS_ENV_VALUE)) {
            if (!CollectionUtils.isEmpty(zsInstances)) {
                return roundRobinChoose(zsInstances);
            } else {
                RequestContextHolder.setEnvTag(BizConstant.DB_ENV_VALUE);
                return roundRobinChoose(dbInstances);
            }
        } else if (envTag.equals(BizConstant.DB_ENV_VALUE)) {
            if (!CollectionUtils.isEmpty(dbInstances)) {
                return roundRobinChoose(dbInstances);
            } else {
                RequestContextHolder.setEnvTag(BizConstant.ZS_ENV_VALUE);
                return roundRobinChoose(zsInstances);
            }
        }

        return roundRobinChoose(instances);
    }

    private Response<ServiceInstance> roundRobinChoose(List<ServiceInstance> instances) {
        // 随机正数值 ++i
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }

}
