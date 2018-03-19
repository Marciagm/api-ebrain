/**
 * 登陆相关接口
 */
package com.ebrain.api.service;

import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.auth.Credentials;
import com.ebrain.api.entities.User;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.UserMapper;
import com.ebrain.api.util.SecurityFilter;
import com.ebrain.api.util.SysConfig;

@Service
public class LoginService {
	private Logger logger = LoggerFactory.getLogger(LoginService.class);

	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 用户登陆
	 * @param form
	 * @return
	 * @throws BaseException
	 */
	public User login(Credentials form)throws BaseException {
		User user=null;
		try {
			user = userMapper.findByUsername(form.getUsername());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
		if(user==null){
			throw new BaseException(1,"用户名或密码错误");
		}
		if(!DigestUtils.md5Hex(DigestUtils.md5Hex(form.getPassword())).equals(user.getPassword())){
			throw new BaseException(1,"用户名或密码错误");
		}
		user.setPassword(null);
		user.setToken(UUID.randomUUID().toString());
		user.setUpdateTime(DateFormatUtils.format(Calendar.getInstance(),"yyyy-MM-dd HH:mm:ss"));
		
		try {
			userMapper.updateByPrimaryKeySelective(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
		return SecurityFilter.filterUser(user);
	}

	/**
	 * 退出登陆
	 * @param accesstoken
	 * @throws BaseException
	 */
	public void logout(String accesstoken) throws BaseException {
		User user=null;
		try {
			user = userMapper.findByToken(accesstoken);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
		if(user==null){
			return;
		}
		user.setToken(""); //设置为空字符串而不是null，null不会被保存
		try {
			userMapper.updateByPrimaryKeySelective(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
	} 
	
	/**
	 * 是否登陆
	 * @param token
	 * @return
	 * @throws BaseException
	 */
	public boolean isLogin(String token)throws BaseException {
		User user=null;
		try {
			user = userMapper.findByToken(token);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
		if(user==null){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 * @throws BaseException
	 */
	public String getLoginUserId(String token)throws BaseException {
		User user=null;
		try {
			user = userMapper.findByToken(token);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
		if(user==null){
			throw new BaseException(1,"未登陆");
		}
		return user.getTid();
	}
	
	/**
	 * 注册
	 * @param user
	 * @return
	 * @throws BaseException
	 */
	public User signup(User user)throws BaseException {
		if(isUsernameExists(user.getUsername())){
			throw new BaseException(1,"该账号已被注册");
		}
		user.setTid(SysConfig.getCreateId());
		if(StringUtils.isEmpty(user.getPassword())){
			user.setPassword("123456");
		}
		user.setPassword(DigestUtils.md5Hex(DigestUtils.md5Hex(user.getPassword())));
		user.setToken(UUID.randomUUID().toString());
		user.setCreateTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
		user.setUpdateTime(user.getCreateTime());
		try {
			userMapper.insertSelective(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"保存异常");
		}
		return SecurityFilter.filterUser(user);
	}
	
	/**
	 * 判断用户名是否存在
	 * @param username
	 * @return
	 */
	public boolean isUsernameExists(String username){
		User user = userMapper.findByUsername(username);
		if(user == null){
			return false;
		}
		return true;
	}
}
