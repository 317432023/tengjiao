package com.tengjiao.part.redis;

import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.tengjiao.part.redis.ModeDict.APP;
import static java.util.stream.Collectors.toList;

/**
 * @author kangtengjiao
 */
@Component
public class RedisTool {

    private Environment env;
    private RedisTemplate redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    public RedisTool(Environment env, RedisTemplate redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.env = env;
        init();
    }

    private String applicationGroup = "", springApplicationName = "";

    private void init() {
        this.applicationGroup = env.getProperty("application.group");
        this.springApplicationName = env.getProperty("spring.application.name");
    }

    /**
     * 在key前面附加前缀
     *
     * @param key
     * @param mode NONE-不带任何应用前缀
     *             APP-使用spring.application.name前缀
     *             APP_GROUP-使用application.group前缀，如果没有使用spring.application.name前缀
     * @return
     */
    public String wrapKey(String key, ModeDict mode) {
        switch (mode) {
            case APP:
                if (springApplicationName != null && !"".equals(springApplicationName)) {
                    return springApplicationName + ":" + key;
                }
                return key;
            case APP_GROUP:
                if (applicationGroup != null && !"".equals(applicationGroup)) {
                    return applicationGroup + ":" + key;
                }
                return wrapKey(key, APP);
            case NONE:
            default:
                return key;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @param mode 0-不带任何应用前缀
     *             1-使用spring.application.name前缀
     *             2-使用application.group.name前缀，如果没有使用spring.application.name前缀
     * @return
     */
    public Boolean expire(String key, ModeDict mode, long time) {
        return redisTemplate.expire(wrapKey(key, mode), time, TimeUnit.SECONDS);
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key  键 不能为null
     * @param mode 0-不带任何应用前缀
     *             1-使用spring.application.name前缀
     *             2-使用application.group.name前缀，如果没有使用spring.application.name前缀
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key, ModeDict mode) {
        return redisTemplate.getExpire(wrapKey(key, mode), TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key  键
     * @param mode 0-不带任何应用前缀
     *             1-使用spring.application.name前缀
     *             2-使用application.group.name前缀，如果没有使用spring.application.name前缀
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key, ModeDict mode) {
        return redisTemplate.hasKey(wrapKey(key, mode));
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(ModeDict mode, String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(wrapKey(key[0], mode));
            } else {
                redisTemplate.delete(
                  Arrays.stream(key).map(e -> wrapKey(e, mode)).collect(toList())
                );
            }
        }
    }
// ============================String=============================

    public String getString(String key, ModeDict mode) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(wrapKey(key, mode));
    }

    public void setString(String key, String value, ModeDict mode) {
        stringRedisTemplate.opsForValue().set(wrapKey(key, mode), value);
    }

    /**
     * 字符串缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public void setString(String key, String value, ModeDict mode, long time) {
        if (time > 0) {
            stringRedisTemplate.opsForValue().set(wrapKey(key, mode), value, time, TimeUnit.SECONDS);
        } else {
            setString(key, value, mode);
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public <T> T get(String key, ModeDict mode) {
        if (key == null) return null;
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(wrapKey(key, mode));
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public <T> void set(String key, final T value, ModeDict mode) {
        redisTemplate.opsForValue().set(wrapKey(key, mode), value);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public <T> void set(String key, T value, ModeDict mode, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(wrapKey(key, mode), value, time, TimeUnit.SECONDS);
        } else {
            set(key, value, mode);
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta, ModeDict mode) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(wrapKey(key, mode), delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta, ModeDict mode) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(wrapKey(key, mode), -delta);
    }
// ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public <T> T hget(String key, String item, ModeDict mode) {
        HashOperations<String, String, T> operation = redisTemplate.opsForHash();
        return operation.get(wrapKey(key, mode), item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public <T> Map<String, T> hmget(String key, ModeDict mode) {
        HashOperations<String, String, T> operation = redisTemplate.opsForHash();
        return operation.entries(wrapKey(key, mode));
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public <T> void hmset(String key, Map<String, T> map, ModeDict mode) {
        HashOperations<String, String, T> operation = redisTemplate.opsForHash();
        operation.putAll(wrapKey(key, mode), map);
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public <T> void hmset(String key, Map<String, T> map, ModeDict mode, long time) {
        hmset(key, map, mode);
        if (time > 0) {
            expire(key, mode, time);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public <T> void hset(String key, String item, T value, ModeDict mode) {
        redisTemplate.opsForHash().put(wrapKey(key, mode), item, value);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public <T> void hset(String key, String item, T value, ModeDict mode, long time) {
        redisTemplate.opsForHash().put(wrapKey(key, mode), item, value);
        if (time > 0) {
            expire(key, mode, time);
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, ModeDict mode, Object... item) {
        redisTemplate.opsForHash().delete(wrapKey(key, mode), item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item, ModeDict mode) {
        return redisTemplate.opsForHash().hasKey(wrapKey(key, mode), item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by, ModeDict mode) {
        return redisTemplate.opsForHash().increment(wrapKey(key, mode), item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by, ModeDict mode) {
        return redisTemplate.opsForHash().increment(wrapKey(key, mode), item, -by);
    }
// ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public <T> Set<T> sGet(String key, ModeDict mode) {
        SetOperations<String, T> operation = redisTemplate.opsForSet();
        return operation.members(wrapKey(key, mode));
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public Boolean sHas(String key, Object value, ModeDict mode) {
        SetOperations<String, Object> operation = redisTemplate.opsForSet();
        return operation.isMember(wrapKey(key, mode), value);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public <T> Long sSet(String key, ModeDict mode, T... values) {
        SetOperations<String, T> operation = redisTemplate.opsForSet();
        return operation.add(wrapKey(key, mode), values);
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public <T> Long sSetAndTime(String key, long time, ModeDict mode, T... values) {
        SetOperations<String, T> operation = redisTemplate.opsForSet();
        Long count = operation.add(wrapKey(key, mode), values);
        if (time > 0) {
            expire(key, mode, time);
        }
        return count;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public Long sGetSetSize(String key, ModeDict mode) {
        return redisTemplate.opsForSet().size(wrapKey(key, mode));
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(String key, ModeDict mode, Object... values) {
        Long count = redisTemplate.opsForSet().remove(wrapKey(key, mode), values);
        return count;
    }
// ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public <T> List<T> lGet(String key, long start, long end, ModeDict mode) {
        return redisTemplate.opsForList().range(wrapKey(key, mode), start, end);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public Long lGetListSize(String key, ModeDict mode) {
        return redisTemplate.opsForList().size(wrapKey(key, mode));
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public <T> T lGetIndex(String key, long index, ModeDict mode) {
        ListOperations<String, T> operation = redisTemplate.opsForList();
        return operation.index(wrapKey(key, mode), index);
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public <T> Long lSet(String key, T value, ModeDict mode) {
        ListOperations<String, T> operation = redisTemplate.opsForList();
        return operation.rightPush(wrapKey(key, mode), value);
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public <T> Long lSet(String key, List<T> value, ModeDict mode) {
        ListOperations<String, T> operation = redisTemplate.opsForList();
        return operation.rightPushAll(wrapKey(key, mode), value);
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public <T> Long lSet(String key, List<T> value, ModeDict mode, long time) {
        Long count = lSet(key, value, mode);
        if (time > 0) {
            expire(key, mode, time);
        }
        return count;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public void lUpdateIndex(String key, long index, Object value, ModeDict mode) {
        redisTemplate.opsForList().set(wrapKey(key, mode), index, value);
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lRemove(String key, long count, Object value, ModeDict mode) {
        return redisTemplate.opsForList().remove(wrapKey(key, mode), count, value);
    }

    /**
     * 模式匹配删除 eg. redisTool.deleteByPattern("users::user:*")
     * @param pattern
     */
    public void deleteByPattern(String pattern, ModeDict mode) {
        Set<String> keys = redisTemplate.keys(wrapKey(pattern, mode));
        if(keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }
}