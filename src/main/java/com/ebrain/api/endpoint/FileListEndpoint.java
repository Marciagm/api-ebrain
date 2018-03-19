package com.ebrain.api.endpoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.entities.FileList;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.service.FileListService;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.SuccessReturn;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;

import io.swagger.annotations.Api;

@Component
@Path("/filelist")
@Api(value = "/filelist")
public class FileListEndpoint {
	private Logger logger = LoggerFactory.getLogger(FileListEndpoint.class);
	
	@Autowired private FileListService fileListService;
	@Autowired private LoginService loginService;
	
	@POST
	@Path("/upload")
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
			File file = new File(path+"/"+fileName);
			FileUtils.copyInputStreamToFile(fileInputStream, file);
			//String url= SysConfig.getProperty("ebrain[imageUrl]", "http://localhost:8080/upload/")+date+"/"+fileName;
			FileList fileList = new FileList();
			fileList.setFileSize(com.ebrain.api.util.FileUtil.renderSize(path+"/"+fileName));
			fileList.setCreateBy(userId);
			fileList.setFilename(disposition.getFileName());
			fileList.setFilepath(file.getAbsolutePath());
			fileListService.create(fileList);
			return Response.status(200).entity(SuccessReturn.build(fileList)).build();
		} catch (IOException e) {
			logger.error("上传文件错误【/common/upload】,err:{}",e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "上传文件错误")).build();
		} catch (BaseException e) {
			logger.error("上传文件错误【/common/upload】,err:{}",e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1,e.getMessage())).build();
		}
	}
	
	
	@GET
	@Path("list")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response list(@HeaderParam("Authorization") String token,
			@QueryParam("filename") String filename,
			@DefaultValue("1") @QueryParam("pageNum") Integer pageNum,
			@DefaultValue("10") @QueryParam("pageSize") Integer pageSize){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	String userId = loginService.getLoginUserId(token);
        	if(StringUtils.isEmpty(userId)){
        		throw new BaseException(1,"未登录");
        	}
        	PageInfo<FileList> page = fileListService.list(filename,userId, pageNum,pageSize);
            return Response.status(200).entity(SuccessReturn.build(page)).build();
        } catch (BaseException e) {
            logger.error("查看文件列表【/filelist/list】,err:{}",  e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("查看文件列表【/filelist/list】,err:{}",e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
	
	
	@GET
	@Path("delete")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response delete(@HeaderParam("Authorization") String token,
			@QueryParam("tid") String tid){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	String userId = loginService.getLoginUserId(token);
        	if(StringUtils.isEmpty(userId)){
        		throw new BaseException(1,"未登录");
        	}
        	fileListService.delete(userId, tid);
            return Response.status(200).entity(SuccessReturn.build("删除成功")).build();
        } catch (BaseException e) {
            logger.error("删除文件【/filelist/delete】,err:{}",  e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("删除文件【/filelist/delete】,err:{}",e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
	
	
	@GET
	@Path("detail")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response detail(@HeaderParam("Authorization") String token,
			@QueryParam("tid") String tid){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	FileList file = fileListService.detail(tid);
            return Response.status(200).entity(SuccessReturn.build(file)).build();
        } catch (BaseException e) {
            logger.error("查看文件【/filelist/detail】,err:{}",  e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("查看文件【/filelist/detail】,err:{}",e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
	
	
	@GET
	@Path("server-choose")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response serverChoose(@HeaderParam("Authorization") String token,
			@QueryParam("parent") String parent){
		try {
			
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	File file=null;
        	if(StringUtils.isEmpty(parent)){
        		file = new File("/");
        	}else{
        		file = new File("/"+parent);
        	}
            return Response.status(200).entity(SuccessReturn.build(file.list())).build();
        } catch (BaseException e) {
            logger.error("查看文件【/filelist/detail】,err:{}",  e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("查看文件【/filelist/detail】,err:{}",e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
	
	
	@GET
	@Path("validate")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response validateFiles(@HeaderParam("Authorization") String token,
			@QueryParam("jobId") String jobId){
		try {
			
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	fileListService.validate(jobId);
            return Response.status(200).entity(SuccessReturn.build("success")).build();
        } catch (BaseException e) {
            logger.error("验证文件【/filelist/validate】,err:{}",  e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("验证文件【/filelist/validate】,err:{}",e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
	
}
