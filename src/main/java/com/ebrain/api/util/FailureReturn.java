package com.ebrain.api.util;

import java.io.Serializable;

public class FailureReturn implements Serializable {
	private static final long serialVersionUID = -4286808143919762978L;

	public static ReturnValue build(int code,String msg){
		ReturnValue rv = new ReturnValue();
		rv.setCode(code);
		rv.setMsg(msg);
		return rv;
	}
}
