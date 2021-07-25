package com.cave.seata.example.cluster.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description: 业务工程 goods
 * @author: Cave
 * @create: 2021-07-25 00:26
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.cave.seata"})
public class ClusterGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClusterGoodsApplication.class, args);
    }

}
