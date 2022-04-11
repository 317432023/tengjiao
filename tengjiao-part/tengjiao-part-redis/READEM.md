# 使用说明

在application.yml 中配置
```yml
spring:
  redis:
    host: localhost
    port: 6379
    password: 15bc104021ea44258202381e32be4cca
    database: 0
    timeout: 10000
    jedis:
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 8
        min-idle: 0
    redisson:
      enable: true
      ## redisson-*.yml 示例文件见 doc 目录
      #config: classpath:config/redisson-single.yml
      config: |
        singleServerConfig:
          idleConnectionTimeout: 10000
          connectTimeout: 10000
          timeout: 3000
          retryAttempts: 3
          retryInterval: 1500
          address: redis://${spring.redis.host}:${spring.redis.port}
          password: ${spring.redis.password}
          clientName: null
          keepAlive: false
          tcpNoDelay: false
          # 单个连接最大订阅数量
          subscriptionsPerConnection: 5
          # 发布和订阅连接的最小空闲连接数
          subscriptionConnectionMinimumIdleSize: 1
          # 发布和订阅连接池大小
          subscriptionConnectionPoolSize: 50
          # 最小空闲连接数
          connectionMinimumIdleSize: 32
          # 连接池大小
          connectionPoolSize: 64
          # 数据库编号
          database: ${spring.redis.database}
          # DNS监测时间间隔，单位：毫秒
          dnsMonitoringInterval: 5000
        threads: 8
        nettyThreads: 8
        codec: !<org.redisson.client.codec.StringCodec> {}
        transportMode: "NIO"
```