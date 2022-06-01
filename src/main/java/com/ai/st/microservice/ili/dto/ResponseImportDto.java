package com.ai.st.microservice.ili.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ImportModel", description = "Response import")
public class ResponseImportDto {

    private Boolean imported;
    private String message;

    public ResponseImportDto() {

    }

    public ResponseImportDto(Boolean imported, String message) {
        super();
        this.imported = imported;
        this.message = message;
    }

    @ApiModelProperty(required = true, notes = "Was the information imported?")
    public Boolean getImported() {
        return imported;
    }

    public void setImported(Boolean imported) {
        this.imported = imported;
    }

    @ApiModelProperty(required = true, notes = "Process message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
