# 简介

需要redis paas在不同region的自动failover，因为两者密码等不一致，需要通过程序来健康检查。
发现异常情况，通过切换连接信息实现切换。

## 依赖版本信息

1. java8的本地安装

2. spring-boot：2.3.3.RELEASE

3. redisson-spring-boot-starter：3.13.2
  
## spring-boot-redisson-failover-sdk说明

1. 一个自动管理的模块。通过RedisFailoverAutoConfiguration来装配redis信息信息。

2. 配置了一个scheduledTask。如果系统中配置了esm.backup.redis.password这个属性，则定时检测，发现异常则切换。

3. 现在默认版本号：1.0.0.RELEASE

## spring-boot-redisson-failover-sdk-usage-example说明

1. 依赖了spring-boot-redisson-failover-sdk工程的一个springboot简单工程，来说明正常使用办法。

2. 配置几个参数：esm.redis.nodes，esm.redis.password，esm.backup.redis.password

3. test case验证预期使用情况