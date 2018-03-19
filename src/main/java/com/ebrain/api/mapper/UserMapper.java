package com.ebrain.api.mapper;

import java.util.List;

import com.ebrain.api.entities.User;

public interface UserMapper {
    int deleteByPrimaryKey(String tid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

	User findByUsername(String username);

	User findByToken(String accesstoken);

	List<User> list(String keyword);
}