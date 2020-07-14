package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ItemRegistralRevisionDto")
public class ItemRegistralRevisionDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(required = true, notes = "Id")
	private Long id;

	@ApiModelProperty(required = true, notes = "New Fmi")
	private String newFmi;

	@ApiModelProperty(required = true, notes = "Old Fmi")
	private String oldFmi;

	@ApiModelProperty(required = true, notes = "Orip")
	private String orip;

	@ApiModelProperty(required = true, notes = "Real estate registration")
	private String realEstateRegistration;

	@ApiModelProperty(required = true, notes = "Nomenclature")
	private String nomenclature;

	@ApiModelProperty(required = true, notes = "Boundary space")
	private String boundarySpace;

	@ApiModelProperty(required = true, notes = "File ID")
	private Long fileId;

	@ApiModelProperty(required = true, notes = "Boundary ID")
	private Long boundaryId;

	@ApiModelProperty(required = true, notes = "Issuing city")
	private String issuingCity;

	@ApiModelProperty(required = true, notes = "Inssuing entity")
	private String issuingEntity;

	@ApiModelProperty(required = true, notes = "Document date")
	private String documentDate;

	@ApiModelProperty(required = true, notes = "Document number")
	private String documentNumber;

	@ApiModelProperty(required = true, notes = "Document type")
	private String documentType;

	public ItemRegistralRevisionDto() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNewFmi() {
		return newFmi;
	}

	public void setNewFmi(String newFmi) {
		this.newFmi = newFmi;
	}

	public String getOldFmi() {
		return oldFmi;
	}

	public void setOldFmi(String oldFmi) {
		this.oldFmi = oldFmi;
	}

	public String getOrip() {
		return orip;
	}

	public void setOrip(String orip) {
		this.orip = orip;
	}

	public String getRealEstateRegistration() {
		return realEstateRegistration;
	}

	public void setRealEstateRegistration(String realEstateRegistration) {
		this.realEstateRegistration = realEstateRegistration;
	}

	public String getNomenclature() {
		return nomenclature;
	}

	public void setNomenclature(String nomenclature) {
		this.nomenclature = nomenclature;
	}

	public String getBoundarySpace() {
		return boundarySpace;
	}

	public void setBoundarySpace(String boundarySpace) {
		this.boundarySpace = boundarySpace;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Long getBoundaryId() {
		return boundaryId;
	}

	public void setBoundaryId(Long boundaryId) {
		this.boundaryId = boundaryId;
	}

	public String getIssuingCity() {
		return issuingCity;
	}

	public void setIssuingCity(String issuingCity) {
		this.issuingCity = issuingCity;
	}

	public String getIssuingEntity() {
		return issuingEntity;
	}

	public void setIssuingEntity(String issuingEntity) {
		this.issuingEntity = issuingEntity;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

}
