package com.ebrain.api.entities;

public class ModelJobWithBLOBs extends ModelJob {
	private String reason;

    private String fileList;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getFileList() {
        return fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList == null ? null : fileList.trim();
    }
}