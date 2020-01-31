package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public class IliExportResultDto implements Serializable {

	private static final long serialVersionUID = 3333909202329876777L;

	private boolean status;
	private Long integrationId;
	private String pathFile;
	private IntegrationStatDto stats;

	public IliExportResultDto() {

	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Long getIntegrationId() {
		return integrationId;
	}

	public void setIntegrationId(Long integrationId) {
		this.integrationId = integrationId;
	}

	public String getPathFile() {
		return pathFile;
	}

	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}

	public IntegrationStatDto getStats() {
		return stats;
	}

	public void setStats(IntegrationStatDto stats) {
		this.stats = stats;
	}

}
