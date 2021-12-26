package com.ai.st.microservice.ili.dto;

import com.ai.st.microservice.ili.business.ConceptBusiness;
import com.ai.st.microservice.ili.business.QueueResponse;

import java.io.Serializable;

public class IlivalidatorBackgroundDto implements Serializable {

    private static final long serialVersionUID = -5774043946431854011L;

    private String pathFile;
    private Long userCode;
    private String versionModel;
    private Boolean skipGeometryValidation;
    private Boolean skipErrors;
    private Long conceptId;
    private String queueResponse;
    private String referenceId;
    private Long requestId;
    private Long supplyRequestedId;
    private String observations;

    public IlivalidatorBackgroundDto() {
        this.versionModel = "3.0";
        this.skipErrors = false;
        this.skipGeometryValidation = false;
        this.conceptId = ConceptBusiness.CONCEPT_OPERATION;
        this.queueResponse = QueueResponse.QUEUE_UPDATE_STATE_XTF_SUPPLIES;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getSupplyRequestedId() {
        return supplyRequestedId;
    }

    public void setSupplyRequestedId(Long supplyRequestedId) {
        this.supplyRequestedId = supplyRequestedId;
    }

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getVersionModel() {
        return versionModel;
    }

    public void setVersionModel(String versionModel) {
        this.versionModel = versionModel;
    }

    public Boolean getSkipErrors() {
        return skipErrors;
    }

    public void setSkipErrors(Boolean skipErrors) {
        this.skipErrors = skipErrors;
    }

    public Boolean getSkipGeometryValidation() {
        return skipGeometryValidation;
    }

    public void setSkipGeometryValidation(Boolean skipGeometryValidation) {
        this.skipGeometryValidation = skipGeometryValidation;
    }

    public Long getConceptId() {
        return conceptId;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }

    public String getQueueResponse() {
        return queueResponse;
    }

    public void setQueueResponse(String queueResponse) {
        this.queueResponse = queueResponse;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
