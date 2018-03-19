/**   
 * <p>本文件仅为内部使用，请勿外传</p>
 */
package com.ebrain.api.util;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * <p>
 * Description: 采用Gson解析
 * </p>
 * 时间: 2016年12月20日 上午10:35:52
 *
 * @author peisong
 * @since v1.0.0
 */
public class JsonUtil {
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	private static Gson gson = new Gson();
	private static JsonParser parser = new JsonParser();

	public static <T> T fromJson(String jsonString, Class<T> t) {
		try {
			return gson.fromJson(jsonString, t);
		} catch (Exception e) {
			logger.error("解析JSON失败:{},{}，err:{}", jsonString, t, e.getMessage());
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * Description: TODO(这里用一句话描述这个类的作用)
	 * </p>
	 * 时间: 2016年12月20日 上午11:29:42
	 *
	 * @author peisong
	 * @since v1.0.0
	 * @param jsonString
	 * @param typeReference
	 * @param typeReference
	 *            传入：new TypeToken<T>() {}
	 * @return T
	 */
	public static <T> T fromJson(String jsonString, Type typeReference) {
		try {
			return gson.fromJson(jsonString, typeReference);
		} catch (Exception e) {
			logger.error("解析JSON失败:{},{}，err:{}", jsonString, typeReference, e.getMessage());
		}
		return null;
	}

	public static <T> T fromJson(JsonElement jsonElement, Class<T> t) {
		try {
			return gson.fromJson(jsonElement, t);
		} catch (Exception e) {
			logger.error("解析JSON失败:{},{}，err:{}", jsonElement, t, e.getMessage());
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * Description: TODO(这里用一句话描述这个类的作用)
	 * </p>
	 * 时间: 2016年12月20日 上午11:29:42
	 *
	 * @author peisong
	 * @since v1.0.0
	 * @param jsonString
	 * @param typeToken
	 * @param typeToken
	 *            传入：new TypeToken<T>() {}.getType()
	 * @return T
	 */
	public static <T> T fromJson(JsonElement jsonElement, Type typeToken) {
		try {
			return gson.fromJson(jsonElement, typeToken);
		} catch (Exception e) {
			logger.error("解析JSON失败:{},{}，err:{}", jsonElement, typeToken, e.getMessage());
		}
		return null;
	}

	public static JsonElement toJsonElement(String jsonString) {
		try {
			return parser.parse(jsonString);
		} catch (Exception e) {
			logger.error("转换JSON失败:{},err:{}", jsonString, e.getMessage());
		}
		return null;
	}

	public static JsonObject toJsonObject(String jsonString) {
		try {
			JsonElement jsonElement = toJsonElement(jsonString);
			if (jsonElement.isJsonObject()) {
				return jsonElement.getAsJsonObject();
			}
		} catch (Exception e) {
			logger.error("转换JSON失败:{},err:{}", jsonString, e.getMessage());
		}
		return null;
	}

	public static JsonArray toJsonArray(String jsonString) {
		try {
			JsonElement jsonElement = toJsonElement(jsonString);
			if (jsonElement.isJsonArray()) {
				return jsonElement.getAsJsonArray();
			}
		} catch (Exception e) {
			logger.error("转换JSON失败:{},err:{}", jsonString, e.getMessage());
		}
		return null;
	}

	public static String toJson(Object value) {
		try {
			return gson.toJson(value);
		} catch (Exception e) {
			logger.error("转换JSON失败:{},err:{}", value, e.getMessage());
		}
		return null;
	}

}
