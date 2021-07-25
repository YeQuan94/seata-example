package com.cave.seata.example.cluster.order.controller;

import com.cave.seata.example.cluster.goods.feign.GoodsServiceFeign;
import com.cave.seata.example.cluster.order.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: OrderController
 * @author: Cave
 * @create: 2021-07-24 18:48
 **/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private GoodsServiceFeign goodsServiceFeign;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/1")
    @Transactional
    public void test1() {
        goodsServiceFeign.update();
        orderMapper.insert();
        int i = 1/0;
    }

    @GetMapping("/2")
    @GlobalTransactional
    public void test2() {
        goodsServiceFeign.update();
        orderMapper.insert();
        int i = 1/0;
    }

    @GetMapping("/3")
    public void test3() {
        goodsServiceFeign.update();
        orderMapper.insert();
    }

}
