package com.tengjiao.part.redis;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisGeoTool {
  
  private StringRedisTemplate redisTemplate;

  public RedisGeoTool(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 添加经纬度信息
   * redis 命令: geoadd <key> <lat> <lng> <member>
   */
  public Long geoAdd(String key, Point point, String member) {
    if(redisTemplate.hasKey(key)) {
      redisTemplate.opsForGeo().remove(key, member);
    }
    return redisTemplate.opsForGeo().add(key, point, member);
 
  }
  
  /**
   * 查找指定key的经纬度信息
   * redis命令：geopos <key> <member>
   */
  public List<Point> geoGet(String key, String... members) {
    return redisTemplate.opsForGeo().position(key, members);
  }

  /**
   * 返回两个位置的距离
   * redis 命令：geodist <key> <member1> <member2>
   */
  public List<Point> geoDist(String key, String member1, String member2, Metric metric) {
    return redisTemplate.opsForGeo().position(key, member1, member2);
  }
  
  /**
   * 给定经纬度，返回半径不超过指定距离的元素
   * redis 命令：georadius <key> <lat> <lng> <number> <metrics> WITHDIST WITHCOORD ASC
   */
  public GeoResults<RedisGeoCommands.GeoLocation<String>> nearByXY(String key, Circle circle, long count) {
    RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
      .includeDistance() // 返回结果包含距离
      .includeCoordinates() // 返回结果包含经纬度
      .sortAscending() // 排序方式：正序
      .limit(count) // 限定返回的记录数
    ;
    return redisTemplate.opsForGeo().radius(key, circle, args);
  }

  /**
   * 给定经纬度，返回半径不超过指定距离的元素
   * redis 命令：georadiusbymember <key> <member> <number> <metrics> WITHDIST WITHCOORD ASC
   */
  public GeoResults<RedisGeoCommands.GeoLocation<String>> nearByPlace(String key, String member, Distance distance, long count) {
    RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
      .includeDistance() // 返回结果包含距离
      .includeCoordinates() // 返回结果包含经纬度
      .sortAscending() // 排序方式：正序
      .limit(count) // 限定返回的记录数
    ;
    return redisTemplate.opsForGeo().radius(key, member, distance, args);
  }
  
  /**
   * geohash
   * redis 命令：geohash <key> <member>
   *
   */
  public List<String> geoHash(String key, String member) {
    return redisTemplate.opsForGeo().hash(key, member);
  }
}