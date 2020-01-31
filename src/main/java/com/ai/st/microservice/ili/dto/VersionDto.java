package com.ai.st.microservice.ili.dto;

import java.io.Serializable;
import java.util.Date;

public class VersionDto implements Serializable {

	private static final long serialVersionUID = -953611847535475293L;

	private Long id;
	private String name;
	private Date createdAt;

	public VersionDto() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
