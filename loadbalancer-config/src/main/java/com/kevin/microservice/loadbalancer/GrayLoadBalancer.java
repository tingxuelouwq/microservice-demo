package com.kevin.microservice.loadbalancer;

import com.kevin.microservice.common.BizConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(RoundRobinLoadBalancer.class);

    final AtomicInteger position;

    final String serviceId;

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     */
    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                  String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     * @param seedPosition Round Robin element position marker
     */
    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
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

        RequestDataContext dataContext = (RequestDataContext) request.getContext();
        HttpHeaders headers = dataContext.getClientRequest().getHeaders();

        // 判断是否为总社请求
        if (headers.get(BizConstant.ENV) == null ||
                headers.get(BizConstant.ENV).get(0).equals(BizConstant.ZS)) {

            // 总社节点请求，得到总社服务实例列表
            List<ServiceInstance> findInstances = instances.stream()
                    .filter(instance -> instance.getMetadata().get(BizConstant.ENV) == null
                            || instance.getMetadata().get(BizConstant.ENV).equals(BizConstant.ZS))
                    .collect(Collectors.toList());

            if (findInstances.size() > 0) {
                instances = findInstances;
            } else {    // 总社服务实例列表为空，则访问东坝服务实例列表
                List<ServiceInstance> findInstances2 = instances.stream()
                        .filter(instance -> instance.getMetadata().get(BizConstant.ENV) != null
                                && instance.getMetadata().get(BizConstant.ENV).equals(BizConstant.DB))
                        .collect(Collectors.toList());
                if (findInstances2.size() > 0) {
                    instances = findInstances2;
                    headers.set(BizConstant.ENV, BizConstant.DB);
                }
            }
        } else {
            // 非总社请求，访问东坝服务实例列表
            List<ServiceInstance> findInstances = instances.stream()
                    .filter(instance -> instance.getMetadata().get(BizConstant.ENV) != null
                            && instance.getMetadata().get(BizConstant.ENV).equals(BizConstant.DB))
                    .collect(Collectors.toList());

            if (findInstances.size() > 0) {
                instances = findInstances;
            } else {    // 东坝服务实例列表为空，则访问总社服务实例列表
                List<ServiceInstance> findInstances2 = instances.stream()
                        .filter(instance -> instance.getMetadata().get(BizConstant.ENV) == null
                                || instance.getMetadata().get(BizConstant.ENV).equals(BizConstant.ZS))
                        .collect(Collectors.toList());
                if (findInstances2.size() > 0) {
                    instances = findInstances2;
                    headers.set(BizConstant.ENV, BizConstant.ZS);
                }
            }
        }

        // 随机正数值 ++i
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}
