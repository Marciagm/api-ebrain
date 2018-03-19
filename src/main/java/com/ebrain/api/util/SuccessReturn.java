package com.ebrain.api.util;

import java.io.Serializable;

import com.google.gson.Gson;

public class SuccessReturn implements Serializable {

	private static final long serialVersionUID = -6634140268217232560L;

	public static Object build(Object data){
		ReturnValue rv = new ReturnValue();
		rv.setCode(0);
		rv.setData(data);
		rv.setMsg("success");
		Gson gson = new Gson();
		return gson.toJson(rv);
	}
}
