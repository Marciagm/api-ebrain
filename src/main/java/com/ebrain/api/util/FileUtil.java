package com.ebrain.api.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;

public class FileUtil {
	public static String renderSize(String f){
		try {
			File file = new File(f);
			Path p = Paths.get(file.getAbsolutePath());
			BasicFileAttributes a = Files.getFileAttributeView(p, BasicFileAttributeView.class)
			        .readAttributes();
			return FileUtils.byteCountToDisplaySize(a.size());
			
		} catch (IOException e) {
			return "-";
		}
	}
	
	public static void main(String[] args) {
		System.out.println(renderSize("D:\\BaiduYunDownload\\office_Visio_Premium_2010w_SP1_W32.iso"));
	}
}
