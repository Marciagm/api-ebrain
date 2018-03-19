package com.ebrain.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ebrain.api.exceptions.BaseException;

/**
 * 
 * <p>
 * Description: 读写配置文件
 * </p>
 * 时间: 2016年7月14日 下午1:42:31
 *
 * @author peisong
 * @since v1.0.0
 */
public class PropertiesUtils {
	public static Properties readPropertiesFile(String name) {
		File file = new File(PropertiesUtils.class.getResource("/" + name).getFile());
		return readPropertiesFile(file);
	}

	public static Properties readPropertiesFile(File file) {
		if (!file.exists()) {
			return new Properties();
		}
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
			if (file.getName().toLowerCase().endsWith("xml")) {
				properties.loadFromXML(inputStream);
			} else {
				properties.load(reader);
			}
			inputStream.close(); // 关闭流
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	// 写资源文件，含中文
	public static void writePropertiesFile(File file, Map<String, String> map) {
		Properties properties = new Properties();
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			OutputStream outputStream = new FileOutputStream(file);
			for (Map.Entry<String, String> entry : map.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
			if (file.getName().toLowerCase().endsWith("xml")) {
				properties.storeToXML(outputStream, "author: ebrain", "utf-8");
			} else {
				properties.store(outputStream, "author: ebrain");
			}
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 追加到原有配置文件上
	 * </p>
	 * 时间: 2016年7月14日 下午2:03:50
	 *
	 * @author peisong
	 * @since v1.0.0
	 * @param file
	 * @param map
	 * @param apend
	 *            true:追加到原文件中，false:重写文件
	 * @param replace
	 *            true:替换原配置文件中相同属性值，false:不替换 void
	 */
	public static void apendToPropertiesFile(File file, Map<String, String> map, boolean apend, boolean replace) {
		if (apend) {
			Properties prop = readPropertiesFile(file);
			for (Entry<Object, Object> entry : prop.entrySet()) {
				if (!replace && !map.containsKey(entry.getKey())) {
					map.put(entry.getKey().toString(), entry.getValue().toString());
				}
			}
		}
		writePropertiesFile(file, map);
	}

	public static void main(String[] args) throws BaseException {
		File file = new File(PropertiesUtils.class.getResource("/sys-defualt.properties").getFile());

		Properties prop = readPropertiesFile(file);
		String key = prop.getProperty("ebrain[secure][key]");

		System.out.println(" ============ ");

		for (Entry<Object, Object> entry : prop.entrySet()) {
			String entryKey = entry.getKey().toString();
			String entryValue = entry.getValue().toString();
			if (StringUtils.isNotEmpty(entryValue) && StringUtils.isNotEmpty(key)
					&& !"ebrain[secure][key]".equals(entryKey) && !"ebrain[secure][code]".equals(entryKey)) {
				entryValue = AESUtils.encryptBase64(key, entryValue);
			}
			System.out.println(entryKey + " = " + entryValue);
			prop.setProperty(entryKey, entryValue);
		}
		System.out.println(" ============ ");
		List<String> list = new ArrayList<String>();
		for (Entry<Object, Object> entry : prop.entrySet()) {
			String entryKey = entry.getKey().toString();
			String entryValue = entry.getValue().toString();
			list.add(entryKey + " = " + entryValue);
		}
		// 排序
		list.sort((String h1, String h2) -> h1.compareTo(h2));
		for (String str : list) {
			System.out.println(str);
		}

	}

}
