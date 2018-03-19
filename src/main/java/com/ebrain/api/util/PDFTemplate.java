package com.ebrain.api.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;

import com.ebrain.api.entities.DataCheckResultWithBLOBs;
import com.ebrain.api.entities.ModelExplain;
import com.ebrain.api.exceptions.BaseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFTemplate {

	private String templatePdfPath;

	public PDFTemplate() {
		this.templatePdfPath = SysConfig.getProperty("ebrain[pdf_template_path]", "D:\\test\\pdf\\tpl_v1.pdf");
	}

	public String getTemplatePdfPath() {
		return templatePdfPath;
	}

	public void setTemplatePdfPath(String templatePdfPath) {
		this.templatePdfPath = templatePdfPath;
	}

	
	public File gen(DataCheckResultWithBLOBs dataCheckResult,ModelExplain modelExplain,String output, Map<String, String> images) throws BaseException {
		File file=null;
		Document document=null;
		try {
			 file = new File(output + "\\report.pdf");
             document = new Document(PageSize.A4.rotate()); 
             PdfWriter.getInstance(document, new FileOutputStream(file));

            //设置字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);   
            com.itextpdf.text.Font FontChinese24 = new com.itextpdf.text.Font(bfChinese, 24, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese18 = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD); 
            com.itextpdf.text.Font FontChinese16 = new com.itextpdf.text.Font(bfChinese, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese12 = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font FontChinese11Bold = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese11 = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.ITALIC);
            FontChinese11.setColor(BaseColor.RED);
            com.itextpdf.text.Font FontChinese11Normal = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL);

            document.open();
            //标题
            PdfPTable table1 = new PdfPTable(1);
            PdfPCell cell11 = new PdfPCell(new Paragraph("预测报告",FontChinese24));
            cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell11.setBorder(0);
            //设置每列宽度比例   
            table1.getDefaultCell().setBorder(0);
            table1.addCell(cell11);  
            document.add(table1);
            //加入空行
            Paragraph blankRow1 = new Paragraph(18f, " ", FontChinese18); 
            document.add(blankRow1);

            //预测总览
            PdfPTable table2 = new PdfPTable(1);
            PdfPCell cell21 = new PdfPCell(new Paragraph("一、预测总览",FontChinese18));
            cell21.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell21.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell21.setBorder(0);
            table2.addCell(cell21);  
            document.add(table2);
            //加入空行
            Paragraph blankRow2 = new Paragraph(18f, " ", FontChinese18); 
            document.add(blankRow2);
           
            //模型概要
            PdfPTable table3 = new PdfPTable(1);
            PdfPCell cell31 = new PdfPCell(new Paragraph("      1. 模型概要",FontChinese16));
            cell31.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell31.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell31.setBorder(0);
            table3.addCell(cell31);  
            document.add(table3);
            //加入空行
            Paragraph blankRow3 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow3);
            
            //模型概要
            JsonObject modelExplainJson = JsonUtil.toJsonObject(modelExplain.getModelExplain());
            JsonObject model = modelExplainJson.get("models").getAsJsonArray().get(0).getAsJsonObject();
            String model_name=model.get("model_name").getAsString();
            //开始计算准确率
            float threshold=model.get("precise_recall").getAsJsonObject().get("threshold").getAsFloat();
            JsonArray precise_recall=model.get("precise_recall").getAsJsonObject().get("x_y").getAsJsonArray();
            float dat=Float.MAX_VALUE;
            float dd=0;
            int index = 0;
            for(int i=0;i<precise_recall.size();i++){
            	dd = Math.abs(precise_recall.get(i).getAsJsonObject().get("pctr").getAsFloat()-threshold);
            	if(dd<dat){
            		dat = dd;
            		index = i;
            	}
            }
            threshold = precise_recall.get(index).getAsJsonObject().get("precise").getAsFloat();
            String precise =  String .format("%.2f",threshold*100)+"%";
            //结束计算准确率
            String auc= model.get("roc").getAsJsonObject().get("auc").getAsString();
            String modelFileSize=FileUtil.renderSize(model.get("model_path").getAsString());
            
            PdfPTable table4 = new PdfPTable(10);
            int width4[] = {150,60,120,80,120,80,60,80,140,60};
            table4.setWidths(width4);
            PdfPCell cell41 = new PdfPCell(new Paragraph("训练总耗时：",FontChinese11Bold));
            cell41.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell41.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell41.setBorder(0);
            table4.addCell(cell41); 
            PdfPCell cell42 = new PdfPCell(new Paragraph("-",FontChinese11));
            cell42.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell42.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell42.setBorder(0);
            table4.addCell(cell42); 
            PdfPCell cell43 = new PdfPCell(new Paragraph("算法名称：",FontChinese11Bold));
            cell43.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell43.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell43.setBorder(0);
            table4.addCell(cell43); 
            PdfPCell cell44 = new PdfPCell(new Paragraph(model_name,FontChinese11));
            cell44.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell44.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell44.setBorder(0);
            table4.addCell(cell44); 
            PdfPCell cell45 = new PdfPCell(new Paragraph("准确率：",FontChinese11Bold));
            cell45.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell45.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell45.setBorder(0);
            table4.addCell(cell45); 
            PdfPCell cell46 = new PdfPCell(new Paragraph(precise,FontChinese11));
            cell46.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell46.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell46.setBorder(0);
            table4.addCell(cell46); 
            PdfPCell cell47 = new PdfPCell(new Paragraph("AUC:",FontChinese11Bold));
            cell47.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell47.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell47.setBorder(0);
            table4.addCell(cell47); 
            PdfPCell cell48 = new PdfPCell(new Paragraph(auc,FontChinese11));
            cell48.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell48.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell48.setBorder(0);
            table4.addCell(cell48); 
            PdfPCell cell49 = new PdfPCell(new Paragraph("模型大小：",FontChinese11Bold));
            cell49.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell49.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell49.setBorder(0);
            table4.addCell(cell49); 
            PdfPCell cell410 = new PdfPCell(new Paragraph(modelFileSize,FontChinese11));
            cell410.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell410.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell410.setBorder(0);
            table4.addCell(cell410); 
            document.add(table4);
            //加入空行
            Paragraph blankRow4 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow4);
            
            
            //模型概要
            PdfPTable table5 = new PdfPTable(1);
            PdfPCell cell51 = new PdfPCell(new Paragraph("      2. 特征重要性",FontChinese16));
            cell51.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell51.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell51.setBorder(0);
            table5.addCell(cell51);  
            document.add(table5);
            //加入空行
            Paragraph blankRow5 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow5);
            
            
            //模型概要
            PdfPTable table6 = new PdfPTable(2);
            PdfPCell cell61 = new PdfPCell(new Paragraph("预测结果是由特征来决定的，\r\n哪些特征对结果影响最大，\r\n可通过右图的特征重要性图表来进行解释",FontChinese11Normal));
            cell61.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell61.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell61.setPaddingLeft(30f);
            cell61.setBorder(0);
            table6.addCell(cell61); 
            String topNPath = images.get("feature_importance_chart");
            Image topNimage = Image.getInstance(topNPath);
            table6.addCell(topNimage);
            document.add(table6);
            //加入空行
            Paragraph blankRow6 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow6);
            document.add(blankRow6);
            document.add(blankRow6);
            //二、预测详情
            PdfPTable table7 = new PdfPTable(1);
            PdfPCell cell71 = new PdfPCell(new Paragraph("二、预测详情",FontChinese18));
            cell71.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell71.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell71.setBorder(0);
            table7.addCell(cell71);  
            table7.getDefaultCell().setBorder(0);
            document.add(table7);
            //加入空行
            Paragraph blankRow7 = new Paragraph(18f, " ", FontChinese18); 
            document.add(blankRow7);
            
            //数据文件概况
            PdfPTable table8 = new PdfPTable(1);
            table8.getDefaultCell().setBorder(0);
            PdfPCell cell81 = new PdfPCell(new Paragraph("      1. 数据文件概况",FontChinese16));
            cell81.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell81.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell81.setBorder(0);
            table8.addCell(cell81);  
            document.add(table8);
            //加入空行
            Paragraph blankRow8 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow8);
            
            
            PdfPTable table9 = new PdfPTable(2);
            table9.getDefaultCell().setBorder(0);
            PdfPTable table9_1 = new PdfPTable(4);
            PdfPCell cell9_1 = new PdfPCell(new Paragraph("文件大小：",FontChinese11Bold));
            cell9_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell9_1.setBorder(0);
            table9_1.addCell(cell9_1);
            
            JsonObject dataCheckResultJson = JsonUtil.toJsonObject(dataCheckResult.getDataResult());
            
            PdfPCell cell9_2 = new PdfPCell(new Paragraph(dataCheckResultJson.get("file_size").getAsString(),FontChinese11));
            cell9_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell9_2.setBorder(0);
            table9_1.addCell(cell9_2);
            
            PdfPCell cell9_3 = new PdfPCell(new Paragraph("行数：",FontChinese11Bold));
            cell9_3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell9_3.setBorder(0);
            table9_1.addCell(cell9_3);
            
            PdfPCell cell9_4 = new PdfPCell(new Paragraph(dataCheckResultJson.get("total_instance_count").getAsString(),FontChinese11));
            cell9_4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_4.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell9_4.setBorder(0);
            table9_1.addCell(cell9_4);
            
            PdfPCell cell9_5 = new PdfPCell(new Paragraph("训练样本数：",FontChinese11Bold));
            cell9_5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell9_5.setBorder(0);
            table9_1.addCell(cell9_5);
            
            PdfPCell cell9_6 = new PdfPCell(new Paragraph(dataCheckResultJson.get("validate_instance_count").getAsString(),FontChinese11));
            cell9_6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_6.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell9_6.setBorder(0);
            table9_1.addCell(cell9_6);
            
            PdfPCell cell9_7 = new PdfPCell(new Paragraph("预测样本数：",FontChinese11Bold));
            cell9_7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_7.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell9_7.setBorder(0);
            table9_1.addCell(cell9_7);
            
            PdfPCell cell9_8 = new PdfPCell(new Paragraph("-",FontChinese11));
            cell9_8.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_8.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell9_8.setBorder(0);
            table9_1.addCell(cell9_8);
            
            PdfPCell cell9_9 = new PdfPCell(new Paragraph("异常数据：",FontChinese11Bold));
            cell9_9.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_9.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell9_9.setBorder(0);
            table9_1.addCell(cell9_9);
            PdfPCell cell9_10 = new PdfPCell(new Paragraph(dataCheckResultJson.get("except_instance_count").getAsString(),FontChinese11));
            cell9_10.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_10.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell9_10.setBorder(0);
            table9_1.addCell(cell9_10);
            
            PdfPCell cell9_11 = new PdfPCell(new Paragraph("特征列数：",FontChinese11Bold));
            cell9_11.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_11.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell9_11.setBorder(0);
            table9_1.addCell(cell9_11);
            PdfPCell cell9_12 = new PdfPCell(new Paragraph(dataCheckResultJson.get("column_count").getAsString(),FontChinese11));
            cell9_12.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9_12.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell9_12.setBorder(0);
            table9_1.addCell(cell9_12);
            
            table9.addCell(table9_1);
            
            String featurePath = images.get("feature_chart");
            Image featureimage = Image.getInstance(featurePath);
            
            table9.addCell(featureimage);
            
            document.add(table9);
            //加入空行
            Paragraph blankRow9 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow9);
            //结束数据文件概况
            PdfPTable table10 = new PdfPTable(1);
            table10.getDefaultCell().setBorder(0);
            PdfPCell cell10_1 = new PdfPCell(new Paragraph("      2. 局部统计表",FontChinese16));
            cell10_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell10_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell10_1.setBorder(0);
            table10.addCell(cell10_1);  
            document.add(table10);
            
            Paragraph blankRow10 = new Paragraph(18f, " ", FontChinese18); 
            document.add(blankRow10);
            
            //处理表格数据
            String[] columnName={"特征","特征类型","唯一个数","缺少情况","最大值","最小值","平均值","方差"};
    		Object[][] rowData=null;
    		//dataCheckResultJson = JsonUtil.toJsonObject(dataCheckResult.getDataResult());
    		JsonArray feature_and_label = dataCheckResultJson.get("feature_and_label").getAsJsonArray();
    		int type = feature_and_label.get(0).getAsJsonObject().get("type").getAsInt();
    		if(type==0){
    			if(rowData==null){
    				rowData = new Object[feature_and_label.size()-1][8];
    			}
    			for(int i=1;i<feature_and_label.size();i++){
    				
    				JsonObject json = feature_and_label.get(i).getAsJsonObject();
    				Object[] ss = new Object[8];
    				if(json.has("feature_name")){
    					ss[0]=json.get("feature_name").getAsString();
    				}else{
    					ss[0]="-";
    				}
    				if(json.has("type")){
    					ss[1]=json.get("type").getAsString();
    				}else{
    					ss[1]="-";
    				}
    				if(json.has("value_count")){
    					ss[2]=json.get("value_count").getAsString();
    				}else{
    					ss[2]="-";
    				}
    				if(json.has("miss")){
    					ss[3]=json.get("miss").getAsString();
    				}else{
    					ss[3]="-";
    				}
    				if(json.has("max")){
    					ss[4]=json.get("max").getAsString();
    				}else{
    					ss[4]="-";
    				}
    				if(json.has("min")){
    					ss[5]=json.get("min").getAsString();
    				}else{
    					ss[5]="-";
    				}
    				if(json.has("mean")){
    					ss[6]=json.get("mean").getAsString();
    				}else{
    					ss[6]="-";
    				}
    				if(json.has("std")){
    					ss[7]=json.get("std").getAsString();
    				}else{
    					ss[7]="-";
    				}
    				rowData[i-1]=ss;
    			}
    		}
            
            PdfPTable table11 = new PdfPTable(8);
            table11.getDefaultCell().setBorder(0);
            for(int i=0;i<8;i++){
	            PdfPCell cell11_1 = new PdfPCell(new Paragraph(columnName[i],FontChinese11Bold));
	            cell11_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell11_1.setHorizontalAlignment(Element.ALIGN_LEFT);
	            cell11_1.setBackgroundColor(BaseColor.GRAY);
	            table11.addCell(cell11_1);  
            }
            for(int i=0;i<rowData.length;i++){
	            PdfPCell cell0 = new PdfPCell(new Paragraph(rowData[i][0].toString(),FontChinese11Normal));
	            cell0.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell0.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell0); 
	            PdfPCell cell1 = new PdfPCell(new Paragraph(rowData[i][1].toString(),FontChinese11Normal));
	            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell1);
	            PdfPCell cell2 = new PdfPCell(new Paragraph(rowData[i][2].toString(),FontChinese11Normal));
	            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell2);
	            PdfPCell cell3 = new PdfPCell(new Paragraph(rowData[i][3].toString(),FontChinese11Normal));
	            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell3);
	            PdfPCell cell4 = new PdfPCell(new Paragraph(rowData[i][4].toString(),FontChinese11Normal));
	            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell4);
	            PdfPCell cell5 = new PdfPCell(new Paragraph(rowData[i][5].toString(),FontChinese11Normal));
	            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell5);
	            PdfPCell cell6 = new PdfPCell(new Paragraph(rowData[i][6].toString(),FontChinese11Normal));
	            cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell6);
	            PdfPCell cell7 = new PdfPCell(new Paragraph(rowData[i][7].toString(),FontChinese11Normal));
	            cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
	            table11.addCell(cell7);
            }
            document.add(table11);
           //加入空行
            Paragraph blankRow11_1 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow11_1);
            
            
            //准确&召回图
            PdfPTable table12 = new PdfPTable(1);
            PdfPCell cell12_1 = new PdfPCell(new Paragraph("      3. 准确&召回率",FontChinese16));
            cell12_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell12_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell12_1.setBorder(0);
            table12.addCell(cell12_1);  
            document.add(table12);
            //加入空行
            Paragraph blankRow12 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow12);
            
            PdfPTable table13 = new PdfPTable(2);
            String recallChartPath = images.get("recall_chart");
            Image recallChartImate = Image.getInstance(recallChartPath);
            table13.addCell(recallChartImate);
            
            PdfPCell cell13_1 = new PdfPCell(new Paragraph("准确率：预测为正的样本中有多少是真正的正样本，红色表示，\r\n召回率：是针对我们原来的样本而言的，它表示的是样本中的正例有多少被预测正确了，蓝色表示。",FontChinese11Normal));
            cell13_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell13_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell13_1.setPaddingLeft(30f);
            cell13_1.setBorder(0);
            table13.addCell(cell13_1); 
            
            document.add(table13);
            //加入空行
            Paragraph blankRow13 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow13);
            
            //混淆矩阵
            PdfPTable table14 = new PdfPTable(1);
            PdfPCell cell14_1 = new PdfPCell(new Paragraph("      4. 混淆矩阵",FontChinese16));
            cell14_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell14_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell14_1.setBorder(0);
            table14.addCell(cell14_1);  
            document.add(table14);
            //加入空行
            Paragraph blankRow14 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow14);
            
            
            
            JsonArray confusion_matrix = model.get("confusion_matrix").getAsJsonArray();
            
            PdfPTable table15 = new PdfPTable(confusion_matrix.get(0).getAsJsonArray().size()+1);
            //表头
            PdfPCell cell15_01 = new PdfPCell(new Paragraph("",FontChinese11Normal));
            cell15_01.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell15_01.setHorizontalAlignment(Element.ALIGN_LEFT);
            /*cell21_01.setRowspan(2);
            cell21_01.setColspan(2);*/
            table15.addCell(cell15_01); 
            
            PdfPCell cell151_02 = new PdfPCell(new Paragraph("真实值",FontChinese11Normal));
            cell151_02.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell151_02.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell151_02.setColspan(confusion_matrix.get(0).getAsJsonArray().size());
            table15.addCell(cell151_02);
            
            PdfPCell cell15_03 = new PdfPCell(new Paragraph("预测值",FontChinese11Normal));
            cell15_03.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell15_03.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell15_03.setRowspan(confusion_matrix.size()+1);
            table15.addCell(cell15_03);
            
            for(int i=0;i<confusion_matrix.size();i++){
            	JsonArray json  =confusion_matrix.get(i).getAsJsonArray();
            	
            	for(int j=0;j<json.size();j++){
            		String v = json.get(j).getAsJsonObject().get("value").getAsString();
            		if(json.get(j).getAsJsonObject().has("percent")){
            			v = v+"\r\n("+json.get(j).getAsJsonObject().get("percent").getAsString()+")";
            		}
		            PdfPCell cell151_1 = new PdfPCell(new Paragraph(v,FontChinese11Normal));
		            cell151_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		            cell151_1.setHorizontalAlignment(Element.ALIGN_CENTER);
		            
		            table15.addCell(cell151_1); 
            	}
            }
            document.add(table15);
            Paragraph blankRow15 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow15);
            
            
            //ROC
            PdfPTable table16 = new PdfPTable(1);
            PdfPCell cell16_1 = new PdfPCell(new Paragraph("      5. ROC",FontChinese16));
            cell16_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell16_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell16_1.setBorder(0);
            table16.addCell(cell16_1);  
            document.add(table16);
            //加入空行
            Paragraph blankRow16 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow16);
            
            PdfPTable table17 = new PdfPTable(2);
            String rocChartPath = images.get("roc_chart");
            Image rocChartImate = Image.getInstance(rocChartPath);
            table17.addCell(rocChartImate);
            
            PdfPCell cell17_1 = new PdfPCell(new Paragraph("横轴FPR: FPR越大，预测正类中实际负类越多。\r\n纵轴TPR： TPR越大，预测正类中实际正类越多。\r\n理想目标：TPR=1，FPR=0,即图中(0,1)点，故ROC曲线越靠拢(0,1)点，越偏离45度对角线越好，Sensitivity、Specificity越大效果越好。",FontChinese11Normal));
            cell17_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell17_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell17_1.setPaddingLeft(30f);
            cell17_1.setBorder(0);
            table17.addCell(cell17_1); 
            
            document.add(table17);
            //加入空行
            Paragraph blankRow17 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow17);
          
            
            PdfPTable table18 = new PdfPTable(1);
            PdfPCell cell18_1 = new PdfPCell(new Paragraph("      6. 提升图",FontChinese16));
            cell18_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell18_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell18_1.setBorder(0);
            table18.addCell(cell18_1);  
            document.add(table18);
            //加入空行
            Paragraph blankRow18 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow18);
            
            //提升图
            PdfPTable table19 = new PdfPTable(2);
            String liftChartPath = images.get("lift_chart");
            Image liftChartImate = Image.getInstance(liftChartPath);
            table19.addCell(liftChartImate);
            
            PdfPCell cell19_1 = new PdfPCell(new Paragraph("提升图是对模型训练效果的一个评估，\r\n是模型捕捉到的所有正响应，\r\n对比真实分类情况的表现。\r\n横坐标是把训练集数据分成100等份，\r\n纵坐标是每一等份数据预测效果提升率，\r\n蓝色线是基准线，即随机预测的效果",FontChinese11Normal));
            cell19_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell19_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell19_1.setPaddingLeft(30f);
            cell19_1.setBorder(0);
            table19.addCell(cell19_1); 
            
            document.add(table19);
            //加入空行
            Paragraph blankRow19 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow19);

            //KS
            PdfPTable table20 = new PdfPTable(1);
            PdfPCell cell20_1 = new PdfPCell(new Paragraph("      7. KS",FontChinese16));
            cell20_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell20_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell20_1.setBorder(0);
            table20.addCell(cell20_1);  
            document.add(table20);
            //加入空行
            Paragraph blankRow20 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow20);
            
            PdfPTable table21 = new PdfPTable(2);
            String ksChartPath = images.get("ks_chart");
            Image ksChartImate = Image.getInstance(ksChartPath);
            table21.addCell(ksChartImate);
            
            PdfPCell cell21_1 = new PdfPCell(new Paragraph("红色TPR: 所有真实的“1”中，有多少被模型成功选出，\r\n蓝色FPR: 所有真实的“0”中，有多少被模型误判为1了，\r\n横坐标为样本数量，纵坐标为预测的准确率",FontChinese11Normal));
            cell21_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell21_1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell21_1.setPaddingLeft(30f);
            cell21_1.setBorder(0);
            table21.addCell(cell21_1); 
            
            document.add(table21);
            //加入空行
            Paragraph blankRow21 = new Paragraph(18f, " ", FontChinese16); 
            document.add(blankRow21);
            
            return file;
        }catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(1,"生成PDF失败");
		}finally {
			file=null;
			document.close();
		}
    }
	
}