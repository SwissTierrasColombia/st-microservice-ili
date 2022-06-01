package com.ai.st.microservice.ili.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.dto.QueryDto;
import com.ai.st.microservice.ili.dto.QueryTypeDto;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.dto.VersionDto;
import com.ai.st.microservice.ili.entities.ConceptEntity;
import com.ai.st.microservice.ili.entities.ModelEntity;
import com.ai.st.microservice.ili.entities.QueryEntity;
import com.ai.st.microservice.ili.entities.QueryTypeEntity;
import com.ai.st.microservice.ili.entities.VersionConceptEntity;
import com.ai.st.microservice.ili.entities.VersionEntity;
import com.ai.st.microservice.ili.exceptions.BusinessException;
import com.ai.st.microservice.ili.models.services.IConceptService;
import com.ai.st.microservice.ili.models.services.IVersionService;

@Component
public class VersionBusiness {

    @Autowired
    private IVersionService versionService;

    @Autowired
    private IConceptService conceptService;

    public VersionDataDto getDataVersion(String versionName, Long conceptId) throws BusinessException {

        VersionDataDto versionDataDto;

        VersionEntity versionEntity = versionService.getVersionByName(versionName);

        ConceptEntity conceptEntity = conceptService.getConceptById(conceptId);

        if (versionEntity != null && conceptEntity != null) {

            versionDataDto = new VersionDataDto();
            versionDataDto.setVersion(versionName);

            VersionConceptEntity versionConcept = versionEntity.getVersionsConcepts().stream()
                    .filter(vC -> vC.getConcept().getId().equals(conceptId)).findAny().orElse(null);

            StringBuilder models = new StringBuilder();
            for (ModelEntity modelEntity : versionConcept.getModels()) {
                models.append(modelEntity.getName()).append(";");
            }

            for (QueryEntity queryEntity : versionConcept.getQuerys()) {
                QueryDto queryDto = new QueryDto();
                queryDto.setId(queryEntity.getId());
                queryDto.setQuery(queryEntity.getQuery());
                QueryTypeEntity queryTypeEntity = queryEntity.getQueryType();
                queryDto.setQueryType(new QueryTypeDto(queryTypeEntity.getId(), queryTypeEntity.getName()));
                versionDataDto.getQueries().add(queryDto);
            }

            versionDataDto.setUrl(versionConcept.getUrl());
            versionDataDto.setModels(models.toString());

        } else {
            throw new BusinessException("No se ha encontrado la versión");
        }

        return versionDataDto;
    }

    public List<VersionDto> getAvailableVersions() throws BusinessException {

        List<VersionDto> versionsDto = new ArrayList<>();

        List<VersionEntity> versionsEntity = versionService.getVersions();

        for (VersionEntity versionEntity : versionsEntity) {
            VersionDto versionDataDto = new VersionDto();
            versionDataDto.setId(versionEntity.getId());
            versionDataDto.setName(versionEntity.getName());
            versionDataDto.setCreatedAt(versionEntity.getCreatedAt());

            versionsDto.add(versionDataDto);
        }

        return versionsDto;
    }

}
