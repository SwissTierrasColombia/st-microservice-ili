package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public class ResultImportDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean result;
    private String pathFile;
    private String reference;

    public ResultImportDto() {

    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

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

}
