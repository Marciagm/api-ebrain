package com.ebrain.api.exceptions;

import com.ebrain.api.util.ReturnValue;

public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 66228184664856672L;
	private int errorCode=1;
	private String message="";
	
	public ReturnValue build(){
		ReturnValue rv = new ReturnValue();
		rv.setCode(errorCode);
		rv.setMsg(message);
		return rv;
	}
	public BaseException(){
	}
	public BaseException(int code,String msg){
		this.errorCode = code;
		this.message = msg;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
