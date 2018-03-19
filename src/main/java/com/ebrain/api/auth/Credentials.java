package com.ebrain.api.auth;

public class Credentials {
	private String username;
    private String password;
    
    private String validKey;
    
    private String validCode;
    
    private String ip;
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getValidKey() {
		return validKey;
	}
	public void setValidKey(String validKey) {
		this.validKey = validKey;
	}
	public String getValidCode() {
		return validCode;
	}
	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
    
	
}
