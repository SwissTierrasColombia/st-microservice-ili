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

}
