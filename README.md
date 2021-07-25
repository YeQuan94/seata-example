这是一个 Seata 搭建案例工程，主要用于快速入门 Seata



工程一共分为两个大块（**Seata 单机、集群部署**），每个大块包含三个关键点（**注册中心、配置方式、部署方式**）

说明：

1.本地学习、开发环境、测试环境等可以使用 Seata 单机部署方式；生产环境为了高可用需要采用集群部署方式

2.目前注册中心使用 Eureka 和 Nacos 比较多，因此展示这两种注册中心案例

3.如果注册中心使用的是 Nacos，那可以把 Nacos 当作 Seata 配置中心；使用 Eureka 则用文件方式

4.部署方式展示容器部署；直接启动 Seata 服务

|                | 注册中心 | 配置方式  | 部署方式     |
| -------------- | -------- | --------- | ------------ |
| Seata 单机部署 | Eureka   | file.conf | seata-server |
| Seata 集群部署 | Nacos    | Nacos     | Docker       |

（★）Seata 单机部署流程文档：seata-single

（★）Seata 集群部署流程文档：seata-cluster

-------

Seata 版本 1.4.2

Spring Boot 版本 2.3.12.RELEASE

Spring Cloud 版本 Hoxton.SR11

Spring Cloud Alibaba 版本 2.2.0.RELEASE



需要注意：

Seata 不同版本配置项会有所不同，尽量找到同版本的案例来参考



（吐槽下，Seata 目前的官方文档写得不是很全，有些地方引用的 Seata 版本太久，参考时注意一下）