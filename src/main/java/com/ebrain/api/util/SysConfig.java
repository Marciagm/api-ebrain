/**   
 * <p>本文件仅为内部使用，请勿外传</p>
 */
package com.ebrain.api.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebrain.api.exceptions.BaseException;

/**
 * <p>
 * Description: TODO(这里用一句话描述这个类的作用)
 * </p>
 * 时间: 2016年7月3日 下午10:11:20
 *
 * @author peisong
 * @since v1.0.0
 */
public class SysConfig {
	static Logger logger = LoggerFactory.getLogger(SysConfig.class);
	private static String configFile = "sys-defualt.properties";
	private static Properties pro = PropertiesUtils.readPropertiesFile(configFile);
	private static Map<String, String> config = new HashMap<String, String>();
	// 配置安全码，参数与安全码匹配时，系统退出
	private static final String secureCode = "ebrain[secure][code]";
	// 配置密钥，对参数解密
	private static final String secureKEY = "ebrain[secure][key]";
	private static final String secureKEYValue;
	private static final IdWorker idWorker = new IdWorker(1, 0);

	// 用于AES解密的密钥
	public static final String AESKEY = "4fdrettdf3ygh89kghdf";
	public static final String PRE_TOKEN = "ebrain";
	static {
		// 初始化KEY
		secureKEYValue = getProperty(secureKEY, null);
	}

	public static String getProperty(String key, String defaultValue) {
		String value = config.get(key);
		if (null == value) {
			value = pro.getProperty(key);
			if (null == value || "".equals(value.trim())) {
				logger.warn("key:{}没有配置，默认为：{},请到:{}进行配置，", key, defaultValue, configFile);
				return defaultValue;
			} else if (secureCode.equals(key) || secureKEY.equals(key)) {
				// 这两个key不进行加解密
				config.put(key, value);
				return value;
			} else if (StringUtils.isNotEmpty(secureKEYValue)) {
				try {
					value = AESUtils.decryptBase64(secureKEYValue, value);
					config.put(key, value);
				} catch (BaseException e) {
					logger.error("key【{}】参数【{}】解密码出错,停止启动", key, value);
					System.exit(0);
				}
			}
		}
		return value;
	}

	// 对配置参数做安全验证,防止非法修改的参数生效
	public static boolean ValidSecureCode() {
		String secureValue = getProperty(secureCode, null);
		if (null == secureValue || "".equals(secureValue)) {
			logger.error("请配置安全码");
			System.exit(0);
			return false;
		}
		String validcode = getCode();
		if (secureValue.equals(validcode)) {
			return true;
		} else {
			logger.error("安全码验证失败,停止启动");
			System.exit(0);
			return false;
		}
	}

	private static String getCode() {
		List<String> list = new ArrayList<String>();
		for (Entry<Object, Object> entry : pro.entrySet()) {
			getProperty(entry.getKey().toString(), null);
			if (!secureCode.equals(entry.getKey())) {
				list.add(entry.getValue() + ";");
			}
		}
		// 排序
		list.sort((String h1, String h2) -> h1.compareTo(h2));
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s);
		}
		return DigestUtils.md5Hex(sb.toString());
	}

	public static String getCurTime() {
		Calendar c1 = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(c1.getTime());
	}

	public static String getCurDate() {
		Calendar c1 = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(c1.getTime());
	}

	/**
	 * 
	 * <p>
	 * Description: 返回一下36进制字符串当注册验证码
	 * </p>
	 * 时间: 2016年7月3日 下午7:42:32
	 *
	 * @author peisong
	 * @since v1.0.0
	 * @return String
	 */
	public static String getValidCode() {
		return Long.toString(idWorker.nextId(), 36);
	}

	public static String getCreateId() {
		return String.valueOf(idWorker.nextId());
	}

	public static void main(String[] args) {
		String code = getCode();
		System.out.println(code);
		System.out.println(ValidSecureCode());
	}
}
