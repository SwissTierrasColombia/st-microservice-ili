package com.ai.st.microservice.ili.models.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.entities.VersionEntity;
import com.ai.st.microservice.ili.models.repositories.VersionRepository;

@Service
public class VersionService implements IVersionService {

    @Autowired
    private VersionRepository versionRepository;

    @Override
    public Long getCount() {
        return versionRepository.count();
    }

    @Override
    @Transactional
    public VersionEntity createVersion(VersionEntity versionEntity) {
        return versionRepository.save(versionEntity);
    }

    @Override
    public VersionEntity getVersionByName(String name) {
        return versionRepository.findByName(name);
    }

    @Override
    public List<VersionEntity> getVersions() {
        return versionRepository.findAll();
    }

}
