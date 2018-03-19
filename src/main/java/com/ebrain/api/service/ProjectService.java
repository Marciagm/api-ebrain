/**
 * 项目管理相关业务实现
 */
package com.ebrain.api.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.Project;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.ProjectMapper;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ProjectService {
	private Logger logger = LoggerFactory.getLogger(ProjectService.class);
	@Autowired
	private ProjectMapper projectMapper;

	/**
	 * 新建项目
	 * @param userId
	 * @param record
	 * @return
	 * @throws BaseException
	 */
	public Project create(String userId, Project record) throws BaseException {
		try {
			List<Project> list = projectMapper.list(userId);
			for(Project p : list){
				if(p.getProjectName().equals(record.getProjectName())){
					throw new BaseException(1,"模型名称已存在!");
				}
			}
			record.setTid(SysConfig.getCreateId());
			record.setCreateTime(SysConfig.getCurTime());
			record.setCreateBy(userId);
			projectMapper.insertSelective(record);
			return record;
		}catch(BaseException eb){
			throw eb;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 更新
	 * @param userId
	 * @param record
	 * @return
	 * @throws BaseException
	 */
	public Project update(String userId, Project record) throws BaseException {
		try {
			projectMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
		return null;
	}

	/**
	 * 查看
	 * @param projectId
	 * @return
	 * @throws BaseException
	 */
	public Project detail(String projectId) throws BaseException {
		try {
			if(StringUtils.isEmpty(projectId)){
				throw new BaseException(1, "projectId不能为空");
			}
			return projectMapper.selectByPrimaryKey(projectId);
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 列表查看
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws BaseException
	 */
	public PageInfo<Project> list(String userId,int pageNum,int pageSize) throws BaseException {
		Page<Project> page = PageHelper.startPage(pageNum, pageSize);
		try {
			if(StringUtils.isEmpty(userId)){
				throw new BaseException(1, "userId参数不能为空");
			}
			projectMapper.list(userId);
			return page.toPageInfo();
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 删除项目
	 * @param userId
	 * @param projectId
	 * @throws BaseException
	 */
	public void delete(String userId, String projectId) throws BaseException {
		try {
			Project project = projectMapper.selectByPrimaryKey(projectId);
			if(project==null){
				throw new BaseException(1,"模型不存在");
			}
			project.setStatus("deleted");
			projectMapper.updateByPrimaryKeySelective(project);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
		
	}
}
