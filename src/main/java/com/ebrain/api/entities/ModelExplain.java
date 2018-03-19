package com.ebrain.api.entities;

public class ModelExplain {
    private String tid;

    private String projectId;

    private String jobId;

    private Integer jobSequence;

    private String algorithmName;

    private String modelExplain;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid == null ? null : tid.trim();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId == null ? null : jobId.trim();
    }

    public Integer getJobSequence() {
        return jobSequence;
    }

    public void setJobSequence(Integer jobSequence) {
        this.jobSequence = jobSequence;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName == null ? null : algorithmName.trim();
    }

    public String getModelExplain() {
        return modelExplain;
    }

    public void setModelExplain(String modelExplain) {
        this.modelExplain = modelExplain == null ? null : modelExplain.trim();
    }
}