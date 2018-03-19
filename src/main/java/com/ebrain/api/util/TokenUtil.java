package com.ebrain.api.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.exceptions.BaseException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class TokenUtil {
	static Logger logger =LoggerFactory.getLogger(TokenUtil.class);
	static Set<String> notKickOffClients=new HashSet<String>();
	{
		notKickOffClients.add("wxmp");
		notKickOffClients.add("wxmini");
	}
	@Autowired
	private JedisUtil jedisUtil;

	private final static String appTokenSeparator="_app_";
	public static String getTokenFromHeader(ContainerRequestContext requestContext) {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted
		// correctly
		if (authorizationHeader == null || !authorizationHeader.startsWith(SysConfig.PRE_TOKEN)) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring(SysConfig.PRE_TOKEN.length()).trim();
		return token;
	}

	public void login(String token, String tid,String ip) throws BaseException {
		jedisUtil.set(coverTokenKey(token), tid);
		String memberTokenListKey="TokenKey:memberId:"+tid;
		String json = jedisUtil.get(memberTokenListKey);
		Gson gson = new Gson();
		if (null != json) {
			List<String> tokenlist = gson.fromJson(json, new TypeToken<List<String>>() {
			}.getType());
			if(null!=tokenlist){
				Iterator<String> it=tokenlist.iterator();
				while(it.hasNext()){
					if(!jedisUtil.exists(coverTokenKey(it.next()))){
						it.remove();
					}
				}
			}else{
				tokenlist=new ArrayList<String>();
			}
			if (!tokenlist.contains(token)) {
				tokenlist.add(token);
				jedisUtil.set(memberTokenListKey, gson.toJson(tokenlist), 24 * 60 * 30);
			}
		} else {
			List<String> tokenlist = new ArrayList<String>();
			tokenlist.add(token);
			jedisUtil.set(memberTokenListKey, gson.toJson(tokenlist), 24 * 60 * 30);
		}
		jedisUtil.set("ip:"+tid+":"+ip, token);
	}
	/**
	 * 
	 * <p>
	 * Description: app登录处理，
	 * </p>
	 * 时间: 2016年12月1日 上午11:08:38
	 *
	 * @author peisong
	 * @since v1.0.0 
	 * @param token
	 * @param tid
	 * @param client
	 * @throws BaseException
	 * void
	 */
	public void appLogin(String token, String tid,String client,boolean kickOff) throws BaseException {
		logger.info("用户【{}】，{}端登录，token:{}",tid,client,token);
		jedisUtil.set(coverTokenKey(token), tid);
		String memberTokenListKey="TokenKey:memberId:"+tid;
		String json = jedisUtil.get(memberTokenListKey);
		Gson gson = new Gson();
		if (null != json) {
			List<String> tokenlist = gson.fromJson(json, new TypeToken<List<String>>() {
			}.getType());
			if(kickOff){
				Iterator<String> iter = tokenlist.iterator();
		        while(iter.hasNext()){
		        	//把之前的app的token删除
		            String oldAppToken = iter.next();
		            String logoutToken=null;
		            String oldClient=null;
		            if(oldAppToken.contains(appTokenSeparator)){
		            	oldClient=oldAppToken.substring(oldAppToken.indexOf(appTokenSeparator)+appTokenSeparator.length());
		            	if(notKickOffClients.contains(oldClient)){
		            		continue;
		            	}
		            	logoutToken=oldAppToken.substring(0, oldAppToken.indexOf(appTokenSeparator));
		            	logger.info("用户【{}】，在【{}】端登录，下线【{}】端，token:【{}】成功",tid,client,oldClient,logoutToken);
		            	jedisUtil.del(coverTokenKey(logoutToken));
		            	//imService.systemPush(tid, "用户在其他端登录");
		            	iter.remove();
		            }
		        }
			}
			String appToken=token+appTokenSeparator+client;
			if (!tokenlist.contains(appToken)) {
				tokenlist.add(appToken);
				jedisUtil.set(memberTokenListKey, gson.toJson(tokenlist), 24 * 60 * 30);
			}
		} else {
			List<String> tokenlist = new ArrayList<String>();
			tokenlist.add(token);
			jedisUtil.set(memberTokenListKey, gson.toJson(tokenlist), 24 * 60 * 30);
		}
	}

	public void logout(String token) throws BaseException {
		if(StringUtils.isEmpty(token))
			return ;
		String tid = jedisUtil.get(coverTokenKey(token));
		jedisUtil.del(coverTokenKey(token));
		if (null != tid) {
			List<String> tokenlist = queryToken(tid);
			if (null != tokenlist) {
				Iterator<String> iter = tokenlist.iterator();
		        while(iter.hasNext()){
		            String logoutToken = iter.next();
		            if(logoutToken.contains(appTokenSeparator)){
		            	logoutToken=logoutToken.substring(0, logoutToken.indexOf(appTokenSeparator));
		            }
		            if(token.equals(logoutToken)){
		            	iter.remove();
		            	break;
		            }
		        }
		        String memberTokenListKey="TokenKey:memberId:"+tid;
				if (tokenlist.isEmpty()) {
					jedisUtil.del(memberTokenListKey);
				} else {
					jedisUtil.set(memberTokenListKey, new Gson().toJson(tokenlist), 24 * 60 * 30);
				}
			}else{
				String memberTokenListKey="TokenKey:memberId:"+tid;
				jedisUtil.del(memberTokenListKey);
			}
		}
	}

	public List<String> queryToken(String tid) throws BaseException {
		String memberTokenListKey="TokenKey:memberId:"+tid;
		String json = jedisUtil.get(memberTokenListKey);
		if (null != json) {
			Gson gson = new Gson();
			List<String> tokenlist = gson.fromJson(json, new TypeToken<List<String>>() {
			}.getType());
			return tokenlist;
		} else {
			return null;
		}
	}

	public boolean isLogin(String token) throws BaseException {
		if(StringUtils.isEmpty(token))
			return false;
		return jedisUtil.exists(coverTokenKey(token));
	}
	
	public void validCode(String username,String validKey,String validCode) throws BaseException{
		String failKey="FailLogin:username:"+username;
    	int count=0;
		try{
			String fcount=jedisUtil.get(failKey);
			if(null!=fcount){
				count=Integer.parseInt(fcount);
			}
		}catch(Exception e){
			count=6;
		}
		if(count>5){
			if(null==validKey||"".equals(validKey.trim())
				||null==validCode||"".equals(validCode.trim())
				){
				throw new BaseException(1,"验证码错误");
			}
    		String code=jedisUtil.get(validKey);
    		if(null!=code&&code.equalsIgnoreCase(validCode)){
    			jedisUtil.del(validKey);
    		}else{
    			throw new BaseException(1,"验证码错误");
    		}
    	}
	}
	public int addLoginFail(String username){
		String failKey="FailLogin:username:"+username;
		int count=0;
		try{
			String fcount=jedisUtil.get(failKey);
			if(null!=fcount){
				count=Integer.parseInt(fcount);
			}
		}catch(Exception e){
			count=6;
		}
		count++;
		try {
			jedisUtil.set(failKey, String.valueOf(count),60);
		} catch (BaseException e1) {
		}
		return count;
	}
	public String getIdFromToken(String token) throws BaseException {
		if(StringUtils.isEmpty(token))
			return null;
		return jedisUtil.get(coverTokenKey(token));
	}
	
	public String getIdFromReq(ContainerRequestContext requestContext) throws BaseException {
		String token=getTokenFromHeader(requestContext);
		return jedisUtil.get(coverTokenKey(token));
	}
	
	private String coverTokenKey(String token) {
		token=token.replace(SysConfig.PRE_TOKEN,"").trim();
		return "Token:"+token;
	}
}
