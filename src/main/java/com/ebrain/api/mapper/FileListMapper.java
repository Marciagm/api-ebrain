package com.ebrain.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.FileList;

public interface FileListMapper {
    int deleteByPrimaryKey(String tid);

    int insert(FileList record);

    int insertSelective(FileList record);

    FileList selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(FileList record);

    int updateByPrimaryKey(FileList record);

	List<FileList> list(@Param("filename")String filename,@Param("userId")String userId);

	List<FileList> selectByIdList(List<String> fileList);

	FileList selectByFilePath(String filePath);

	List<FileList> selectByIds(String[] ids);

	List<FileList> fileExist(String projectId, String[] split);
}