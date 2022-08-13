package com.tengjiao.seed.admin.comm;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 分布式雪花ID生成器<br>
 *     注意：即使是同一个应用不同实例也应该保持 workerId+dataCenterId 唯一，否则仍然可能出现id重复
 * https://cloud.tencent.com/developer/article/1624097
 * @author Administrator tengjiao
 */
@Component
public class IdGeneratorSnowflake {
  @Value("${snowflake.workerId:0}")
  private long workerId = 0;     // 机器的终端ID
  @Value("${snowflake.dataCenterId:0}")
  private long dataCenterId = 0; // 机房ID
  private Snowflake snowflake;

  /**
   * 执行顺序：1、构造方法 >>>> 2、@Autowired/@Value >>>> 3、@PostConstruct
   */
  @PostConstruct
  public void init() {
    //try {
    //  workerId = cn.hutool.core.net.NetUtil.ipv4ToLong(cn.hutool.core.net.NetUtil.getLocalhostStr())%32;
    //  System.out.println("当前机器的终端ID:{}" + workerId);
    //} catch (Exception e) {
    //  System.out.println("当前机器的终端ID获取失败" + e);
    //  workerId = cn.hutool.core.net.NetUtil.getLocalhostStr().hashCode()%32;
    //  System.out.println("当前机器的终端ID:{}" + workerId);
    //}
    System.out.println(String.format("当前机器的机房ID:%d，终端ID:%d", dataCenterId, workerId));
    this.snowflake = IdUtil.getSnowflake(workerId, dataCenterId);
  }

  public long snowflakeId(long workerId, long dataCenterId) {
    return IdUtil.getSnowflake(workerId, dataCenterId).nextId();
  }

  public long snowflakeId() {
    if(snowflake == null) {
      snowflake = IdUtil.getSnowflake(workerId, dataCenterId);
    }
    return snowflake.nextId();
  }

}
