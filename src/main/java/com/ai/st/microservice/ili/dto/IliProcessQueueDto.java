package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public class IliProcessQueueDto implements Serializable {

	private static final long serialVersionUID = 645140052635565979L;

	public static final Long VALIDATOR = (long) 1;
	public static final Long INTEGRATOR = (long) 2;
	public static final Long EXPORT = (long) 3;
	public static final Long IMPORT_REFERENCE = (long) 4;
	public static final Long EXPORT_REFERENCE = (long) 5;

	private Long type;
	private IlivalidatorBackgroundDto ilivalidatorData;
	private Ili2pgIntegrationCadastreRegistrationWithoutFilesDto integrationData;
	private Ili2pgExportDto exportData;
	private Ili2pgImportReferenceDto importReferenceData;
	private Ili2pgExportReferenceDto exportReferenceData;

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

	public Ili2pgImportReferenceDto getImportReferenceData() {
		return importReferenceData;
	}

	public void setImportReferenceData(Ili2pgImportReferenceDto importReferenceData) {
		this.importReferenceData = importReferenceData;
	}

	public Ili2pgExportReferenceDto getExportReferenceData() {
		return exportReferenceData;
	}

	public void setExportReferenceData(Ili2pgExportReferenceDto exportReferenceData) {
		this.exportReferenceData = exportReferenceData;
	}

}
