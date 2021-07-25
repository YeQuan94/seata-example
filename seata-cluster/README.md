### Seata 集群部署，注册中心 Nacos，Nacos 配置，Docker 部署



前提知识：可以去 Seata 官网看看一些基本概念，这里不会讲解得很细



涉及到的项目：seata-cluster-goods、seata-cluster-order、seata-cluster-gateway

准备 SQL：查看 seata-cluster.sql 文件



#### 1.下载 Nacos，启动

因为篇幅原因，这里就不讲解 Nacos 基本概念了，所以请自行查看



#### 2.下载 Seata 镜像

对于 Docker 不熟悉的童鞋来说，可以先看看 Docker 入门，熟悉相关概念

Seata 镜像名称是 **seataio/seata-server**，下载版本 1.4.2，当前最新版本



#### 3.准备好 Seata 容器启动环境

Seata 容器启动需要以下内容（seata-server 文件夹）

- seats-server
  - classes
  - libs
  - resources
  - sessionStore

这些文件可以先启动 Seata 容器，然后从容器中复制出来

```shell
docker cp seata:/seata-server /{YOUR_LOCAL_PATH}
# seata，容器名称
# /seata-server，容器中文件
# 主机文件
```

复制出来后可以删除掉刚刚启动的容器



#### 4.修改 Seata 配置文件，并且推送至 Nacos

修改 seata-server/resources 文件夹下的 registry.conf

registry.conf（标有 # MODIFY 是需要修改的）

```
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  # MODIFY
  type = "nacos"

  nacos {
    application = "seata-server"
    # MODIFY
    serverAddr = "host.docker.internal:8848"
    group = "SEATA_GROUP"
    namespace = ""
    # MODIFY
    cluster = "seata-server"
    username = ""
    password = ""
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    application = "default"
    weight = "1"
  }
  redis {
    serverAddr = "localhost:6379"
    db = 0
    password = ""
    cluster = "default"
    timeout = 0
  }
  zk {
    cluster = "default"
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  consul {
    cluster = "default"
    serverAddr = "127.0.0.1:8500"
    aclToken = ""
  }
  etcd3 {
    cluster = "default"
    serverAddr = "http://localhost:2379"
  }
  sofa {
    serverAddr = "127.0.0.1:9603"
    application = "default"
    region = "DEFAULT_ZONE"
    datacenter = "DefaultDataCenter"
    cluster = "default"
    group = "SEATA_GROUP"
    addressWaitTime = "3000"
  }
  file {
    name = "file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  # MODIFY
  type = "nacos"

  nacos {
    # MODIFY
    serverAddr = "host.docker.internal:8848"
    namespace = ""
    group = "SEATA_GROUP"
    username = ""
    password = ""
    dataId = "seataServer.properties"
  }
  consul {
    serverAddr = "127.0.0.1:8500"
    aclToken = ""
  }
  apollo {
    appId = "seata-server"
    ## apolloConfigService will cover apolloMeta
    apolloMeta = "http://192.168.1.204:8801"
    apolloConfigService = "http://192.168.1.204:8080"
    namespace = "application"
    apolloAccesskeySecret = ""
    cluster = "seata"
  }
  zk {
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
    nodePath = "/seata/seata.properties"
  }
  etcd3 {
    serverAddr = "http://localhost:2379"
  }
  file {
    name = "file.conf"
  }
}

```



因为是使用 Nacos 作为 Seata 配置中心，因此不需要修改 file.conf 文件

这里需要把配置文件导入到 Nacos 上，可以参考 Seata 官网文档（http://seata.io/zh-cn/docs/user/configuration/nacos.html）

（这个步骤比较麻烦，如果不愿意的话，也可以继续使用 file.conf 作为配置，效果也是一样的）

（建议先看单机部署，然后再来看集群部署，有些配置在单机讲解了，集群就不讲解了）



a.下载 Seata 1.4.2 服务（后面推送配置需要）

b.下载 config.txt 文件

下载链接：https://github.com/seata/seata/blob/develop/script/config-center/config.txt

放在 seata-server-1.4.2 文件夹下

b.下载 nacos-config.sh 文件

下载链接：https://github.com/seata/seata/tree/develop/script/config-center/nacos

放在 seata-server-1.4.2/conf 文件夹下

c.修改 config.txt 文件（只标记出必须要修改的，其他的按照默认即可）

```tex
store.mode=db
store.db.url=jdbc:mysql://host.docker.internal:3306/seata?useUnicode=true&rewriteBatchedStatements=true
store.db.user=root
store.db.password=123...
service.vgroupMapping.goods-tx-group=seata-server
service.vgroupMapping.order-tx-group=seata-server
```

d.推送 config.txt 文件至 Nacos（以后修改配置直接在 Nacos 上修改即可）

```shell
sh ${SEATAPATH}/script/config-center/nacos/nacos-config.sh -h localhost -p 8848 -g SEATA_GROUP -t 5a3c7d6c-f497-4d68-a71a-2e5e3340b3ca -u username -w password
```

-t 是命名空间 ID，如果没有该参数就是放在 public 下

Nacos 没有开启授权的话，不需要 -u -w 参数

e.登陆 Nacos 查看配置



可以参考 Seata 官方文档：http://seata.io/zh-cn/blog/seata-nacos-analysis.html



#### 5.启动 Seata 集群容器

```shell
docker run -d --name seata-server1 -p 18091:8091 -v /{YOUR_FILE_PATH}:/seata-server seataio/seata-server:1.4.2
docker run -d --name seata-server2 -p 28091:8091 -v /{YOUR_FILE_PATH}:/seata-server seataio/seata-server:1.4.2
docker run -d --name seata-server3 -p 38091:8091 -v /{YOUR_FILE_PATH}:/seata-server seataio/seata-server:1.4.2
# YOUR_FILE_PATH: 就是第三步从 seata 容器中复制出来的路径
```

启动后登陆 Nacos 查看

有些时候启动不了是因为 Docker 和宿主机之间通信问题，这里最好了解一下容器和主机之间是怎么通信的...



#### 6.在业务工程添加 Seata 相关配置

application.yml

```yaml
seata:
  # 与 file.conf service 中名字对应
  tx-service-group: order-tx-group
  application-id: order-seata-id
  enabled: true
  client:
    tm:
      # 一阶段全局提交结果上报TC重试次数，默认1次，建议大于1。
      commitRetryCount: 3
      # 一阶段全局回滚结果上报TC重试次数，默认1次，建议大于1
      rollbackRetryCount: 3
    undo:
      logTable: undo_log  # 自定义undo表名（默认undo_log）
  service:
#    # 事务群组映射
    vgroupMapping:
#      # key = {tx-service-group}，需要对应
#      # value = {seataApplicationName}，Seata 服务注册名称
      order-tx-group: seata-server
  registry:
    type: nacos
    nacos:
#      server-addr: http://127.0.0.1:8848
      server-addr: host.docker.internal:8848
      application: seata-server
  config:
    type: nacos
    nacos:
#      server-addr: http://127.0.0.1:8848
      server-addr: host.docker.internal:8848
  transport:
    shutdown:
      wait: 3
    thread-factory:
      boss-thread-prefix: NettyBoss
      worker-thread-prefix: NettyServerNIOWorker
      server-executor-thread-prefix: NettyServerBizHandler
      share-boss-worker: false
      client-selector-thread-prefix: NettyClientSelector
      client-selector-thread-size: 1
      client-worker-thread-prefix: NettyClientWorkerThread
    type: TCP
    server: NIO
    heartbeat: true
    serialization: seata
    compressor: none
    enable-client-batch-send-request: true # 客户端事务消息请求是否批量合并发送（默认true）
  log:
    exception-rate: 100
```

注意，Seata 配置比较多，有些属性不对是注册不到 Seata 上的，请仔细检查



#### 7.启动业务工程容器，查看是否注册到 Seata

构建 Docker 镜像，seata-cluster-goods、seata-cluster-order、seata-cluster-gateway

启动项目，查看 Nacos 和 Seata 是否有注册

-------

访问 http://127.0.0.1:8000/order/order/1

发生异常后，goods 更新成功，order 新增失败。没有同时回滚

---

访问 http://127.0.0.1:8000/order/order/2

发生异常后，goods 更新失败，order 新增失败。同时回滚

----

访问 http://127.0.0.1:8000/order/order/3

正常逻辑没有异常，goods 更新成功，order 新增成功。同时提交



#### 8.到这里就已经算集群部署完毕，分布式事务正常运行

恭喜恭喜~🎉🎉🎉🎉~

