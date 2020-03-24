package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public class IliProcessQueueDto implements Serializable {

	private static final long serialVersionUID = 645140052635565979L;

	public static final Long VALIDATOR = (long) 1;
	public static final Long INTEGRATOR = (long) 2;
	public static final Long EXPORT = (long) 3;

	private Long type;
	private IlivalidatorBackgroundDto ilivalidatorData;
	private Ili2pgIntegrationCadastreRegistrationWithoutFilesDto integrationData;
	private Ili2pgExportDto exportData;

	public IliProcessQueueDto() {

	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public IlivalidatorBackgroundDto getIlivalidatorData() {
		return ilivalidatorData;
	}

	public void setIlivalidatorData(IlivalidatorBackgroundDto ilivalidatorData) {
		this.ilivalidatorData = ilivalidatorData;
	}

	public Ili2pgIntegrationCadastreRegistrationWithoutFilesDto getIntegrationData() {
		return integrationData;
	}

	public void setIntegrationData(Ili2pgIntegrationCadastreRegistrationWithoutFilesDto integrationData) {
		this.integrationData = integrationData;
	}

	public Ili2pgExportDto getExportData() {
		return exportData;
	}

	public void setExportData(Ili2pgExportDto exportData) {
		this.exportData = exportData;
	}

}
