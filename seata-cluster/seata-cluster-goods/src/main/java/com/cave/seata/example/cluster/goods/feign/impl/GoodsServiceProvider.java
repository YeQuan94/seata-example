package com.cave.seata.example.cluster.goods.feign.impl;

import com.cave.seata.example.cluster.goods.feign.GoodsServiceFeign;
import com.cave.seata.example.cluster.goods.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: GoodsServiceProvider
 * @author: Cave
 * @create: 2021-07-24 18:43
 **/
@RestController
@RequestMapping("/api/goodsService")
public class GoodsServiceProvider implements GoodsServiceFeign {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public void update() {
        goodsMapper.update();
    }

}
