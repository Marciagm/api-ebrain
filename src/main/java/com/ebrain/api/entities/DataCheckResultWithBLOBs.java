package com.ebrain.api.entities;

public class DataCheckResultWithBLOBs extends DataCheckResult {
    private String fileList;

    private String dataResult;

    public String getFileList() {
        return fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList == null ? null : fileList.trim();
    }

    public String getDataResult() {
        return dataResult;
    }

    public void setDataResult(String dataResult) {
        this.dataResult = dataResult == null ? null : dataResult.trim();
    }
}