/**
 * 用户管理相关业务实现
 */
package com.ebrain.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.User;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.UserMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class UserService {
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserMapper userMapper;
	
	public PageInfo<User> list(String keyword,int pageNum,int pageSize) throws BaseException{
		Page<User> page = PageHelper.startPage(pageNum, pageSize);
		try {
			userMapper.list(keyword);
			return page.toPageInfo();
		} catch (Exception e) {
			logger.error("数据库操作错误：err[{}]",e.getMessage());
			throw new BaseException(1,"数据库异常");
		}
	}
}
