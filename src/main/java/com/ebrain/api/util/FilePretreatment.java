package com.ebrain.api.util;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class FilePretreatment {

	public static JSONObject run(String file) throws Exception {
		// 指定读取文件所在位置
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		boolean isFirstLine = false;
		int columnCount = 0; // 第一行的列数
		String separator = ","; // 默认分隔符
		// List<Map<String, String>> rowList = new ArrayList<Map<String,
		// String>>();
		JSONArray rowList = new JSONArray();
		String line = "";
		String newLine ="";
				
		long totalRows = 0l;
		long badTotalRows = 0l;
		
		while ((line = br.readLine()) != null) {
			newLine = line.replace(separator, "");
			String[] cols = line.split(separator);
			if (!isFirstLine) {
				isFirstLine = true;
				columnCount = line.length()-newLine.length();
			}
			if ((line.length()-newLine.length()) != columnCount) {
				badTotalRows++;
				continue;
			}else{
				totalRows++;
			}
			if (totalRows < 100) {
				JSONObject json = new JSONObject();
				for (int i = 0; i < cols.length; i++) {
					json.put("" + i, cols[i]);
				}
				rowList.put(json);
			}else{
				break;
			}

		}
		br.close();
		fr.close();
		JSONObject json = new JSONObject();
		json.put("totalRows", totalRows);
		json.put("badTotalRows", badTotalRows);
		json.put("dataList", rowList);

		return json;

	}

	 /**
	  * 解析文件
	  * @param file
	  * @param separator
	  * @param replace 需要替换的字符串
	  * @param to 替换后的字符串
	  * @param ignoreBadRow true 统计错误行数 并忽略该行
	  * @return
	  * @throws Exception
	  */
	public static JSONObject parsePredictResultFile(String file,String separator,String replace,String to,boolean ignoreBadRow) throws Exception {
		// 指定读取文件所在位置
		//file="D:\\test\\log\\predict_predict_ins";
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		boolean isFirstLine = false;
		int columnCount = 0; // 第一行的列数
		JSONArray rowList = new JSONArray();
		String line = "";
		long badTotalRows = 0l;
		int totalRows=0;
		while ((line = br.readLine()) != null) {
			if (totalRows > 100) {
				break;
			}
			totalRows++;
			if(replace!=null){
				line = line.replaceAll(replace,to);
			}
			String[] cols = line.split(separator);
			if (!isFirstLine) {
				isFirstLine = true;
				columnCount = cols.length;
				if(line.contains("TopN feature")){
					line=line.substring(0, (line.lastIndexOf("TopN feature")-1));
					columnCount--;
				}
			}
			if (ignoreBadRow && cols.length != columnCount) {
				badTotalRows++;
				continue;
			}
			/*JSONObject json = new JSONObject();
			for (int i = 0; i < cols.length; i++) {
				json.put("k_" + i, cols[i]);
			}*/
			JSONArray json = new JSONArray();
			for (int i = 0; i < cols.length; i++) {
				json.put(cols[i]);
			}
			rowList.put(json);
		}
		br.close();
		fr.close();
		JSONObject json = new JSONObject();
		json.put("badTotalRows", badTotalRows);
		json.put("dataList", rowList);

		return json;
	}
	
	public static void main(String[] args) {
		String file = "C:\\Users\\cheny\\Documents\\202286139598442496.predict.filted";
		try {
			
			//JSONObject json = FilePretreatment.run(file);
			//System.out.println(json.toString());
			
			JSONObject json = FilePretreatment.parsePredictResultFile(file, "#|,",":\\d{1,}","",false);
			System.out.println(json.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
