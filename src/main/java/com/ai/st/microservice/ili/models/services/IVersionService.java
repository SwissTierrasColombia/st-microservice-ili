package com.ai.st.microservice.ili.models.services;

import java.util.List;

import com.ai.st.microservice.ili.entities.VersionEntity;

public interface IVersionService {

    public Long getCount();

    public VersionEntity createVersion(VersionEntity versionEntity);

    public VersionEntity getVersionByName(String name);

    public List<VersionEntity> getVersions();

}
