package com.cave.seata.example.goods.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @description: GoodsServiceFeign
 * @author: Cave
 * @create: 2021-07-24 18:41
 **/
@FeignClient(name = "goods", path = "/goods/api/goodsService")
public interface GoodsServiceFeign {

    @PostMapping(value = "/update")
    void update();

}
