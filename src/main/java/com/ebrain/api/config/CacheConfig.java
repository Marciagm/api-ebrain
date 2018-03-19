package com.ebrain.api.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.ebrain.api.util.SysConfig;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * <p>
 * Description: redis缓存配置
 * </p>
 * 时间: 2016年6月27日 下午4:10:52
 *
 * @author peisong
 * @since v1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
	private static Logger logger=LoggerFactory.getLogger(CacheConfig.class);
  private static String host="";//"123.57.135.186";
  private static int port=6379;
  private static int dbindex=0;
  //设置默认缓存过期时间30分钟
  private static int expiration=1800;
  private static String pwd="";
  static{
	  host = SysConfig.getProperty("ebrain[redis][host]", "10.27.241.55");
	  String p = SysConfig.getProperty("ebrain[redis][port]", "6379");
	  port = Integer.valueOf(p);
	  try{
		  port = Integer.valueOf(p);
	  } catch (Exception e) {
		  logger.warn("ebrain[redis][port]配置解析异常,取默认值6379");
		  port=6379;
	  }
	  pwd = SysConfig.getProperty("ebrain[redis][pwd]", "NchCWDka5n3tYaeT");
	  String index = SysConfig.getProperty("ebrain[redis][db]", "0");
	  try{
		  dbindex=Integer.valueOf(index);
	  } catch (Exception e) {
		  logger.warn("ebrain[redis][db]配置解析异常,取默认值0");
		  dbindex=0;
	  }
	  String time = SysConfig.getProperty("ebrain[redis][expiration]", "1800");
	  try{
		  expiration=Integer.valueOf(time);
	  } catch (Exception e) {
		  logger.warn("ebrain[redis][expiration]配置解析异常,取默认值1800");
		  expiration=1800;
	  }
  }
  @Bean
  public JedisConnectionFactory redisConnectionFactory() {
	  JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
	  try {
		  redisConnectionFactory.setDatabase(dbindex);
		  redisConnectionFactory.setHostName(host);
		  redisConnectionFactory.setPassword(pwd);
		  redisConnectionFactory.setPort(port);
		  redisConnectionFactory.setUsePool(true);
		  redisConnectionFactory.setTimeout(5000);
	  } catch (Exception e) {
		  logger.warn("redis初始化异常,err:{}",e.getMessage());
			e.printStackTrace();
		}
   
    return redisConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
    redisTemplate.setConnectionFactory(cf);
    //设置KEY序列化
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(new StringRedisSerializer());
    return redisTemplate;
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
    RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
    cacheManager.setDefaultExpiration(expiration);
    //独立设置部份需要长时间缓存的key的时间，首页向导与游记缓存时间为7天
    Map<String, Long> expires=new HashMap<String, Long>();
    expires.put("travelArticlePage", (long) (7*24*60*60));
    expires.put("appIndexGuider", (long) (7*24*60*60));
	cacheManager.setExpires(expires);
    return cacheManager;
  }
  @Bean
  public KeyGenerator customKeyGenerator() {
    return new KeyGenerator() {
      @Override
      public Object generate(Object o, Method method, Object... objects) {
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getName());
        sb.append(method.getName());
        for (Object obj : objects) {
          sb.append(obj.toString());
        }
        return sb.toString();
      }
    };
  }
  @Bean
  public JedisPoolConfig getJedisPoolConfig(){
	  JedisPoolConfig jedisPoolConfig = redisConnectionFactory().getPoolConfig();
	  return jedisPoolConfig;
  }
  @Bean
  public JedisPool getJedisPool(){
	  JedisPool jedisPool=new JedisPool(redisConnectionFactory().getPoolConfig(), redisConnectionFactory().getShardInfo().getHost(),
			  redisConnectionFactory().getShardInfo().getPort(),redisConnectionFactory().getShardInfo().getConnectionTimeout(),redisConnectionFactory().getShardInfo().getPassword());
	 return  jedisPool;
  }
}
