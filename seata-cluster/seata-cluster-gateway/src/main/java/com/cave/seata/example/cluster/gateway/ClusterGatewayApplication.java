package com.cave.seata.example.cluster.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @description: Gateway 网关工程
 * @author: Cave
 * @create: 2021-07-24 20:10
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class ClusterGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClusterGatewayApplication.class, args);
    }

}
