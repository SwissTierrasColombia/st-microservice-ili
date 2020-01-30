package com.ai.st.microservice.ili.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.dto.VersionDto;
import com.ai.st.microservice.ili.entities.ModelEntity;
import com.ai.st.microservice.ili.entities.VersionEntity;
import com.ai.st.microservice.ili.exceptions.BusinessException;
import com.ai.st.microservice.ili.services.IVersionService;

@Component
public class VersionBusiness {

	@Autowired
	private IVersionService versionService;

	public VersionDto getVersionByName(String name) throws BusinessException {

		VersionDto versionDto = null;

		VersionEntity versionEntity = versionService.getVersionByName(name);
		if (versionEntity instanceof VersionEntity) {

			versionDto = new VersionDto();
			versionDto.setId(versionEntity.getId());
			versionDto.setName(versionEntity.getName());
			versionDto.setUrl(versionEntity.getUrl());

			String models = "";
			for (ModelEntity modelEntity : versionEntity.getModels()) {
				models += modelEntity.getName() + ";";
			}
			versionDto.setModels(models);

		}

		return versionDto;
	}

}
