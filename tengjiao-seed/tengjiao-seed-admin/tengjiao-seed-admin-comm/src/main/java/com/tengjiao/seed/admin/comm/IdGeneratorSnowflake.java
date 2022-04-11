package com.tengjiao.seed.admin.comm;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 分布式雪花ID生成器
 * https://cloud.tencent.com/developer/article/1624097
 */
@Component
public class IdGeneratorSnowflake {
  private long workerId = 0;           // 机器的工作组ID
  private final long dataCenterId = 1; // 机房ID
  private Snowflake snowflake;

  @PostConstruct
  public void init() {
    try {
      workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr())%32;
      System.out.println("当前机器的workerId:{}" + workerId);
    } catch (Exception e) {
      System.out.println("当前机器的workerId获取失败" + e);
      workerId = NetUtil.getLocalhostStr().hashCode()%32;
      System.out.println("当前机器 workId:{}" + workerId);
    }
    this.snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
  }

  public synchronized long snowflakeId() {
    return snowflake.nextId();
  }

  public synchronized long snowflakeId(long workerId, long datacenterId) {
    snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    return snowflake.nextId();
  }

  public static void main(String[] args) {
    // 1236610764324864000
    System.out.println(new IdGeneratorSnowflake().snowflakeId());
  }


}