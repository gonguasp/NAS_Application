package com.nas.service;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoadBalancerService {

    protected static Map<String, Date> loadBalancer = new HashMap<>();

    public String getInstanceUrl(List<ServiceInstance> instances) {
        HashMap<String, Date> aux = new HashMap<>();

        for (ServiceInstance instance : instances) {
            InstanceInfo instanceInfo = ((EurekaServiceInstance) instance).getInstanceInfo();
            String url = instance.getScheme() + "://" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort();
            if (!loadBalancer.containsKey(url)) {
                aux.put(url, new Date(Long.MIN_VALUE));
            } else {
                aux.put(url, loadBalancer.get(url));
            }
        }

        loadBalancer = aux;

        return sortByValue(loadBalancer);
    }

    private String sortByValue(Map<String, Date> hashMap) {
        List<Map.Entry<String, Date>> list = new LinkedList<>(hashMap.entrySet());
        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        hashMap.remove(list.get(0).getKey());
        hashMap.put(list.get(0).getKey(), new Date());

        return list.get(0).getKey();
    }
}