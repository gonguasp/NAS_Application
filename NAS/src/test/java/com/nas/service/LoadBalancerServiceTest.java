package com.nas.service;

import com.netflix.appinfo.InstanceInfo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

class LoadBalancerServiceTest {

    @Test
    @SneakyThrows
    void testLoadUserByUsername() {
        LoadBalancerService service = new LoadBalancerService();
        ServiceInstance serviceInstance = Mockito.mock(EurekaServiceInstance.class);
        Mockito.when(((EurekaServiceInstance) serviceInstance).getInstanceInfo()).thenReturn(Mockito.mock(InstanceInfo.class));
        Mockito.when(serviceInstance.getUri()).thenReturn(new URI(""));
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        serviceInstances.add(serviceInstance);
        assertNotNull(service.getInstanceUrl(serviceInstances));
    }

    @Test
    @SneakyThrows
    void testLoadUserByUsernameElseCasuistic() {
        LoadBalancerService.loadBalancer.put("null://null:0", new Date(Long.MIN_VALUE));
        LoadBalancerService service = new LoadBalancerService();
        ServiceInstance serviceInstance = Mockito.mock(EurekaServiceInstance.class);
        Mockito.when(((EurekaServiceInstance) serviceInstance).getInstanceInfo()).thenReturn(Mockito.mock(InstanceInfo.class));
        Mockito.when(serviceInstance.getUri()).thenReturn(new URI(""));
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        serviceInstances.add(serviceInstance);
        assertNotNull(service.getInstanceUrl(serviceInstances));
    }
}
