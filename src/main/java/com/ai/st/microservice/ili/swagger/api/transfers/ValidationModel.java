package com.ai.st.microservice.ili.swagger.api.transfers;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValidationModel", description = "Response validation")
public class ValidationModel {

	private String resultId;
	private String transfer;
	private Boolean isValid;
	private Boolean log;
	private Boolean xtfLog;

	public ValidationModel() {

	}

	public ValidationModel(String resultId, String transfer, Boolean isValid, Boolean log, Boolean xtfLog) {
		super();
		this.resultId = resultId;
		this.transfer = transfer;
		this.isValid = isValid;
		this.log = log;
		this.xtfLog = xtfLog;
	}

	@ApiModelProperty(required = true, notes = "Result ID")
	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	@ApiModelProperty(required = true, notes = "Transfer")
	public String getTransfer() {
		return transfer;
	}

	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}

	@ApiModelProperty(required = true, notes = "Is valid xtf ?")
	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	@ApiModelProperty(required = true, notes = "Has log ?")
	public Boolean getLog() {
		return log;
	}

	public void setLog(Boolean log) {
		this.log = log;
	}

	@ApiModelProperty(required = true, notes = "Has xtf log ?")
	public Boolean getXtfLog() {
		return xtfLog;
	}

	public void setXtfLog(Boolean xtfLog) {
		this.xtfLog = xtfLog;
	}

}
