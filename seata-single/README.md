### Seata 单机部署，注册中心 Eureka，file.conf 配置，服务启动



前提知识：可以去 Seata 官网看看一些基本概念，这里不会讲解得很细



涉及到的项目：seata-business-goods、seata-business-order、seata-single-eureka、seata-single-gateway

准备 SQL：查看 seata-single.sql 文件



#### 1.去 Seata 仓库下载 seata-server 服务

Seata 发布版本，目前最新版是 1.4.2，https://github.com/seata/seata/releases

下载后解压，得到 seata-server-1.4.2 文件夹



#### 2.修改 file.conf 和 registry.conf 文件

registry.conf（标有 # MODIFY 是需要修改的）

```
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  # MODIFY
  type = "eureka"

  nacos {
    application = "seata-server"
    serverAddr = "127.0.0.1:8848"
    group = "SEATA_GROUP"
    namespace = ""
    cluster = "default"
    username = ""
    password = ""
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    # MODIFY
    application = "seata-server"
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
  type = "file"

  nacos {
    serverAddr = "127.0.0.1:8848"
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



file.conf（标有 # MODIFY 是需要修改的）

注意，因为是单机部署方式，这里没有使用 db 作为事务日志存储，db 在集群部署方式有展示

```
# MODIFY
service {
  # 配置需要使用到 Seata 的项目，目前 goods 和 order 使用到了，因此配置这两个
  # 配置规则：vgroupMapping.{applicationName}-tx-group = #{seataName}
  # applicationName：项目应用名称
  # seataName：Seata 注册名称
  vgroupMapping.goods-tx-group = "seata-server"
  vgroupMapping.order-tx-group = "seata-server"
  # Seata 分组配置
  seata-server.grouplist = "127.0.0.1:8091"
  # 降级开关
  enableDegrade = false
  # 禁用全局事务
  disableGlobalTransaction = false
}

## transaction log store, only used in seata-server
store {
  ## store mode: file、db、redis
  mode = "file"
  ## rsa decryption public key
  publicKey = ""
  ## file store property
  file {
    ## store location dir
    dir = "sessionStore"
    # branch session size , if exceeded first try compress lockkey, still exceeded throws exceptions
    maxBranchSessionSize = 16384
    # globe session size , if exceeded throws exceptions
    maxGlobalSessionSize = 512
    # file buffer size , if exceeded allocate new buffer
    fileWriteBufferCacheSize = 16384
    # when recover batch read size
    sessionReloadReadSize = 100
    # async, sync
    flushDiskMode = async
  }

  ## database store property
  db {
    ## the implement of javax.sql.DataSource, such as DruidDataSource(druid)/BasicDataSource(dbcp)/HikariDataSource(hikari) etc.
    datasource = "druid"
    ## mysql/oracle/postgresql/h2/oceanbase etc.
    dbType = "mysql"
    driverClassName = "com.mysql.jdbc.Driver"
    ## if using mysql to store the data, recommend add rewriteBatchedStatements=true in jdbc connection param
    url = "jdbc:mysql://127.0.0.1:3306/seata?rewriteBatchedStatements=true"
    user = "mysql"
    password = "mysql"
    minConn = 5
    maxConn = 100
    globalTable = "global_table"
    branchTable = "branch_table"
    lockTable = "lock_table"
    queryLimit = 100
    maxWait = 5000
  }

  ## redis store property
  redis {
    ## redis mode: single、sentinel
    mode = "single"
    ## single mode property
    single {
      host = "127.0.0.1"
      port = "6379"
    }
    ## sentinel mode property
    sentinel {
      masterName = ""
      ## such as "10.28.235.65:26379,10.28.235.65:26380,10.28.235.65:26381"
      sentinelHosts = ""
    }
    password = ""
    database = "0"
    minConn = 1
    maxConn = 10
    maxTotal = 100
    queryLimit = 100
  }
}
```



#### 3.启动 seata-server 服务，查看注册中心是否有注册成功

```shell
sh ./seata-server.sh
```

```shell
2021-07-24 18:08:41.281  INFO 3356 --- [nio-8761-exec-2] c.n.e.registry.AbstractInstanceRegistry  : Registered instance SEATA-SERVER/10.1.1.158:seata-server:8091 with status UP (replication=false)
2021-07-24 18:08:41.933  INFO 3356 --- [nio-8761-exec-3] c.n.e.registry.AbstractInstanceRegistry  : Registered instance SEATA-SERVER/10.1.1.158:seata-server:8091 with status UP (replication=true)
```

注册成功



PS：本工程下的 seata-server-1.4.2 就是已经配置好的服务，可以直接启动



#### 4.在业务工程添加 Seata 相关配置

这里需要注意看，一般来说项目启动后疯狂报错，或者注册不到 Seata 上，这些都是因为配置没有对的原因！！

顺便说一下，Seata 数据源和项目数据源要保持一致（Seata 默认支持数据源是：druid，如果你在项目中使用了其他数据源，请在 file.conf 文件中修改 store.db.datasource 属性）

application.yml

```yaml
seata:
  # 与 file.conf service 中名字对应
  tx-service-group: goods-tx-group
  application-id: goods-seata-id
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
    # 事务群组映射
    vgroupMapping:
      # key = {tx-service-group}，需要对应
      # value = {seataApplicationName}，Seata 服务注册名称
      goods-tx-group: seata-server
  registry:
    type: eureka
    eureka:
      service-url: http://localhost:8761/eureka
      application: seata-server
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



#### 5.启动业务工程，查看是否注册到 Seata 上

这里直接启动 goods 和 order 服务即可

```shell
18:24:41.319  INFO --- [rverHandlerThread_1_1_500] i.s.c.r.processor.server.RegRmProcessor  : RM register success,message:RegisterRMRequest{resourceIds='jdbc:mysql://localhost:3306/goods', applicationId='goods-seata-id', transactionServiceGroup='goods-tx-group'},channel:[id: 0xf0186e97, L:/10.1.1.158:8091 - R:/10.1.1.158:53097],client version:1.4.2
18:25:38.993  INFO --- [ttyServerNIOWorker_1_2_16] i.s.c.r.processor.server.RegTmProcessor  : TM register success,message:RegisterTMRequest{applicationId='goods-seata-id', transactionServiceGroup='goods-tx-group'},channel:[id: 0xf1e83575, L:/10.1.1.158:8091 - R:/10.1.1.158:53106],client version:1.4.2
18:28:40.897  INFO --- [lver-bootstrap-executor-0] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
18:31:10.997  INFO --- [rverHandlerThread_1_2_500] i.s.c.r.processor.server.RegRmProcessor  : RM register success,message:RegisterRMRequest{resourceIds='jdbc:mysql://localhost:3306/order', applicationId='order-seata-id', transactionServiceGroup='order-tx-group'},channel:[id: 0x3cb04a64, L:/10.1.1.158:8091 - R:/10.1.1.158:53152],client version:1.4.2
18:32:09.083  INFO --- [ttyServerNIOWorker_1_4_16] i.s.c.r.processor.server.RegTmProcessor  : TM register success,message:RegisterTMRequest{applicationId='order-seata-id', transactionServiceGroup='order-tx-group'},channel:[id: 0xb273a5f5, L:/10.1.1.158:8091 - R:/10.1.1.158:53162],client version:1.4.2
```



#### 6.启动 gateway，goods，order 服务

访问 http://127.0.0.1:9000/order/order/1

发生异常后，goods 更新成功，order 新增失败。没有同时回滚

---

访问 http://127.0.0.1:9000/order/order/2

发生异常后，goods 更新失败，order 新增失败。同时回滚

----

访问 http://127.0.0.1:9000/order/order/3

正常逻辑没有异常，goods 更新成功，order 新增成功。同时提交

----

在 seata 服务日志中也有回滚记录

```shell
19:38:52.033  INFO --- [     batchLoggerPrint_1_1] i.s.c.r.p.server.BatchLogHandler         : SeataMergeMessage timeout=60000,transactionName=test2()
,clientIp:10.1.1.158,vgroup:order-tx-group
19:38:52.033  INFO --- [verHandlerThread_1_22_500] i.s.s.coordinator.DefaultCoordinator     : Begin new global transaction applicationId: order-seata-id,transactionServiceGroup: order-tx-group, transactionName: test2(),timeout:60000,xid:10.1.1.158:8091:2612246258570510342
19:38:52.070  INFO --- [     batchLoggerPrint_1_1] i.s.c.r.p.server.BatchLogHandler         : SeataMergeMessage xid=10.1.1.158:8091:2612246258570510342,branchType=AT,resourceId=jdbc:mysql://localhost:3306/goods,lockKey=seata_goods:1
,clientIp:10.1.1.158,vgroup:goods-tx-group
19:38:52.070  INFO --- [verHandlerThread_1_23_500] i.seata.server.coordinator.AbstractCore  : Register branch successfully, xid = 10.1.1.158:8091:2612246258570510342, branchId = 2612246258570510343, resourceId = jdbc:mysql://localhost:3306/goods ,lockKeys = seata_goods:1
19:38:52.121  INFO --- [     batchLoggerPrint_1_1] i.s.c.r.p.server.BatchLogHandler         : SeataMergeMessage xid=10.1.1.158:8091:2612246258570510342,branchType=AT,resourceId=jdbc:mysql://localhost:3306/order,lockKey=seata_order:5
,clientIp:10.1.1.158,vgroup:order-tx-group
19:38:52.121  INFO --- [verHandlerThread_1_24_500] i.seata.server.coordinator.AbstractCore  : Register branch successfully, xid = 10.1.1.158:8091:2612246258570510342, branchId = 2612246258570510344, resourceId = jdbc:mysql://localhost:3306/order ,lockKeys = seata_order:5
19:38:52.150  INFO --- [     batchLoggerPrint_1_1] i.s.c.r.p.server.BatchLogHandler         : SeataMergeMessage xid=10.1.1.158:8091:2612246258570510342,extraData=null
,clientIp:10.1.1.158,vgroup:order-tx-group
19:38:52.182  INFO --- [verHandlerThread_1_25_500] io.seata.server.coordinator.DefaultCore  : Rollback branch transaction successfully, xid = 10.1.1.158:8091:2612246258570510342 branchId = 2612246258570510344
19:38:52.223  INFO --- [verHandlerThread_1_25_500] io.seata.server.coordinator.DefaultCore  : Rollback branch transaction successfully, xid = 10.1.1.158:8091:2612246258570510342 branchId = 2612246258570510343
19:38:52.224  INFO --- [verHandlerThread_1_25_500] io.seata.server.coordinator.DefaultCore  : Rollback global transaction successfully, xid = 10.1.1.158:8091:2612246258570510342.
```



#### 7.到这里就已经算单机部署完毕，分布式事务正常运行

恭喜恭喜🎉🎉🎉🎉

