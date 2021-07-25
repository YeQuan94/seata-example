package com.cave.seata.example.cluster.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description: 业务工程 order
 * @author: Cave
 * @create: 2021-07-24 22:48
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.cave.seata"})
public class ClusterOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClusterOrderApplication.class, args);
    }

}
