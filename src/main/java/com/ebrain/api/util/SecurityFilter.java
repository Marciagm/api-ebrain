package com.ebrain.api.util;

import com.ebrain.api.entities.User;

public class SecurityFilter {
	
	public static User filterUser(User user){
		user.setPassword(null);
		user.setCompany(null);
		user.setPassword(null);
		user.setPhone(null);
		user.setEmail(null);
		return user;
	}
}
