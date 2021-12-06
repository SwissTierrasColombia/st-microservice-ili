package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public final class ResultSinicImportFile implements Serializable {

    public enum Status {
        IMPORTING,
        SUCCESS_IMPORT,
        FAILED_IMPORT,
    }

    private Status result;
    private String pathFile;
    private String reference;
    private int currentFile;
    private int totalFiles;

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(int currentFile) {
        this.currentFile = currentFile;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public Status getResult() {
        return result;
    }

    public void setResult(Status result) {
        this.result = result;
    }
}
