package com.ebrain.api.util;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.exceptions.BaseException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class JedisUtil {
	/**
	 * 连接池
	 */
	@Autowired
	private JedisPool jedisPool;
	/**
	 * 功能：
	 * 当且仅当 key 不存在，将 key 的值设为 value ，并返回1；
	 * 若给定的 key 已经存在，则 SETNX 不做任何动作，并返回0。
	 * 本方法可用于分布锁
	 * @param key 键
	 * @param value 值
	 * @throws BaseException 
	 */
	public long setnx(String key, String value) throws BaseException {
		Jedis jedis = getJedis();
		try {
			return jedis.setnx(key, value);
		} finally {
			jedis.close();
		}
	}
	/**
	 * 设置缓存
	 * @param key 键
	 * @param value 值
	 * @throws BaseException 
	 */
	public void set(String key, String value) throws BaseException {
		Jedis jedis = getJedis();
		try {
			jedis.set(key, value);
		} finally {
			jedis.close();
		}
	}
	/**
	 * 设置自增计数
	 * @param key 键
	 * @param value 值
	 * @return 
	 * @throws BaseException 
	 */
	public Long incr(String key) throws BaseException {
		Jedis jedis = getJedis();
		try {
			return jedis.incrBy(key,1L);
		} finally {
			jedis.close();
		}
	}
	/**
	 * @param key 键
	 * @param value 值 
	 * @param minute 过期时间 单位分钟
	 * @throws BaseException 
	 */
	public void set(String key, String value,int minute) throws BaseException {
		Jedis jedis = getJedis();
		try {
			jedis.setex(key, minute * 60, value);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 设置过期时间
	 * @param key 键
	 * @param minute 过期时间 单位分钟
	 * @throws BaseException 
	 */
	public void expire(String key, int minute) throws BaseException {
		Jedis jedis = getJedis();
		try {
			jedis.expire(key, minute * 60);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 是否存在键值
	 * @param key 键
	 * @return ture / false
	 * @throws BaseException 
	 */
	public boolean exists(String key) throws BaseException {
		Jedis jedis = getJedis();
		try {
			return jedis.exists(key);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 取得缓存值
	 * @param key 键
	 * @return 返回缓存值
	 * @throws BaseException 
	 */
	public String get(String key) throws BaseException {
		Jedis jedis = getJedis();
		try {
			return jedis.get(key);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 取得缓存值
	 * @param keys 键
	 * @return 返回缓存值
	 * @throws BaseException 
	 */
	public Set<String> keys(String keys) throws BaseException {
		Jedis jedis = getJedis();
		try {
			return jedis.keys(keys);
		} finally {
			jedis.close();
		}
	}
	/**
	 * 清除缓存
	 * @param key 键
	 * @throws BaseException 
	 */
	public void del(String key) throws BaseException {
		Jedis jedis = getJedis();
		try {
			jedis.del(key);
		} finally {
			jedis.close();
		}
	}
	/**
	 * 
	 * <p>
	 * Description: 为支持事务操作，private改为public方法，使用时，需要在最后close：
	 * Jedis jedis = getJedis();
	 * 	try {
	 * 			 使用事务
	 *  		Transaction tx = jedis.multi();
	 *  		操作
	 *  		tx.set("key" , "value" ); 
	 *  		...
	 *  		执行事务
	 *  		List<Object> results = tx.exec();
	 * 		} finally {
	 * 			jedis.close();
	 * 	}
	 * </p>
	 * 时间: 2017年1月19日 上午11:12:56
	 *
	 * @author peisong
	 * @since v1.0.0 
	 * @return
	 * @throws BaseException
	 * Jedis
	 */
	public Jedis getJedis() throws BaseException{
		try{
			return jedisPool.getResource();
		}catch(Exception e){
			throw new BaseException(1,"缓存服务器连接失败");
		}
		
	}
}
