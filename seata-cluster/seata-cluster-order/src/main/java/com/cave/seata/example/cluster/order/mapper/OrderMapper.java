package com.cave.seata.example.cluster.order.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @description: OrderMapper
 * @author: Cave
 * @create: 2021-07-24 19:29
 **/
@Mapper
public interface OrderMapper {

    void insert();

}
