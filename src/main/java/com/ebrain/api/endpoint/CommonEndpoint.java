package com.ebrain.api.endpoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.auth.Secured;
import com.ebrain.api.entities.FileList;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.form.UploadForm;
import com.ebrain.api.service.FileListService;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.SuccessReturn;
import com.ebrain.api.util.SysConfig;

import io.swagger.annotations.Api;

@Component
@Path("/common")
@Api(value = "/common")
public class CommonEndpoint {

	private static Logger log=LoggerFactory.getLogger(CommonEndpoint.class);
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private FileListService fileListService;
	
	@POST 
	@Secured
	@Path("/upload-data")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON+";charset=UTF-8"})
	public Response upload(UploadForm uploadForm) {	
		//参数判断
		try {
			if(null==uploadForm ||uploadForm.getFileData()==null || uploadForm.getFileData().equals("")
					|| (!uploadForm.getFileData().startsWith("data:image/png;base64,") && 
							!uploadForm.getFileData().startsWith("data:image/jpeg;base64,"))){
				throw new BaseException(1,"数据格式错误");
			}
			String url = saveData(uploadForm.getFileData(),uploadForm.getFileName());
			return Response.status(200).entity(SuccessReturn.build(url)).build();
		}catch(BaseException e){
			log.error("上传文件错误【/common/upload-data】,err:{}",e.getMessage());
			return Response.status(200).entity(FailureReturn.build(e.getErrorCode(), e.getMessage())).build();
		}catch(Exception e){
			log.error("上传文件错误【/common/upload-data】,err:{}",e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "上传文件错误")).build();
		}
	}
	
	private String saveData(String fileBase64, String filename) throws Exception {
		if (StringUtils.isEmpty(fileBase64)) {
			throw new NullPointerException("fileBase64 不能为null");
		} else {
			File tempImg = null;
			try {
				fileBase64 = fileBase64.replaceFirst("data:image/png;base64,", "");
				fileBase64 = fileBase64.replaceFirst("data:image/jpeg;base64,", "");
				byte[] byteimg = Base64.getDecoder().decode(fileBase64.trim());
				for (int i = 0; i < byteimg.length; ++i) {
					if (byteimg[i] < 0) {// 调整异常数据
						byteimg[i] += 256;
					}
				}
				String ext=".jpg";
				if(filename.lastIndexOf(".")>0){
					ext=filename.substring(filename.lastIndexOf("."));
				}
				String date=DateFormatUtils.format(new Date(), "yyyyMMdd");
				String path =SysConfig.getProperty("ebrain[uploadpath]", "")+date;
				tempImg = new File(path);
				if(!tempImg.exists()){
					tempImg.mkdirs();
				}
				String fileName = UUID.randomUUID().toString()+ext;
				tempImg = new File(path+"/"+fileName);
				FileOutputStream fos = new FileOutputStream(tempImg);
				fos.write(byteimg);
				fos.flush();
				fos.close();
				
				return SysConfig.getProperty("ebrain[imageUrl]", "")+date+"/" +fileName;

			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception("上传图片失败");
			}finally{
				fileBase64=null;
			}
		}
	}
	
	@POST
	@Path("/upload-file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition disposition,
			@Context HttpServletRequest request,
			@HeaderParam("Authorization") String token) {
		File tempImg;
		try {
			String userId = loginService.getLoginUserId(token);
        	if(StringUtils.isEmpty(userId)){
        		throw new BaseException(1,"未登录");
        	}
        	
			String ext = ".txt";
			if (disposition.getFileName().lastIndexOf(".") > 0) {
				ext = disposition.getFileName().substring(disposition.getFileName().lastIndexOf("."));
			}
			String date=DateFormatUtils.format(new Date(), "yyyyMMdd");
			String fileName = UUID.randomUUID().toString()+ext;
			String defaultPath = request.getSession().getServletContext().getRealPath("/");
			String path = SysConfig.getProperty("ebrain[uploadpath]",defaultPath+"/upload/")+date;
			tempImg = new File(path);
			if (!tempImg.exists()) {
				tempImg.mkdirs();
			}
			FileUtils.copyInputStreamToFile(fileInputStream, new File(path+"/"+fileName));
			String url= SysConfig.getProperty("ebrain[imageUrl]", "http://localhost:8080/upload/")+date+"/"+fileName;
			FileList fileList = new FileList();
			fileList.setCreateBy(userId);
			fileList.setFilename(disposition.getFileName());
			fileList.setFilepath(path+"/"+fileName);
			fileListService.create(fileList);
			return Response.status(200).entity(SuccessReturn.build("success")).build();
		} catch (IOException e) {
			log.error("上传文件错误【/common/upload-file】,err:{}",e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "上传文件错误")).build();
		} catch (BaseException e) {
			log.error("上传文件错误【/common/upload-file】,err:{}",e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1,e.getMessage())).build();
		}

	} 
	
	
	@POST 
	@Path("/upload-image")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response uploadImage(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("file") FormDataContentDisposition disposition,
			@Context HttpServletRequest request,@QueryParam("ticket") String ticket) {	
		//参数判断
		try {
			if(StringUtils.isEmpty(ticket)){
				Response.status(Response.Status.UNAUTHORIZED).entity(FailureReturn.build(1, "未登录")).build();
    		}
    		if(!loginService.isLogin(ticket)){
    			Response.status(Response.Status.UNAUTHORIZED).entity(FailureReturn.build(1, "未登录")).build();
	   		}
			InputStream is = file.getValueAs(InputStream.class);
			String ext = ".pdf";
			if (disposition.getFileName().lastIndexOf(".") > 0) {
				ext = disposition.getFileName().substring(disposition.getFileName().lastIndexOf("."));
			}
			String date=DateFormatUtils.format(new Date(), "yyyyMMdd");
			String fileName = UUID.randomUUID().toString()+ext;
			String defaultPath = request.getSession().getServletContext().getRealPath("/");
			String path = SysConfig.getProperty("ebrain[uploadpath]",defaultPath+"/upload/")+date+"/practice";
			File temp = new File(path+"/"+fileName);
			temp = new File(path);
			if (!temp.exists()) {
				temp.mkdirs();
			}
			FileUtils.copyInputStreamToFile(is, new File(path+"/"+fileName));
			String url= SysConfig.getProperty("ebrain[imageUrl]", "http://localhost:8080/upload/")+date+"/practice/"+fileName;
			Thread.sleep(1000);
			return Response.status(200).entity(SuccessReturn.build(url)).build();
		}catch(Exception e){
			log.error("上传文件错误【/common/upload-image】,err:{}",e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "上传文件错误")).build();
		}
	}
}
