package com.ai.st.microservice.ili.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.entities.VersionConceptEntity;
import com.ai.st.microservice.ili.repositories.VersionConceptRepository;

@Service
public class VersionConceptService implements IVersionConceptService {

    @Autowired
    private VersionConceptRepository versionConceptRepository;

    @Override
    @Transactional
    public VersionConceptEntity createVersionConcept(VersionConceptEntity versionConcept) {
        return versionConceptRepository.save(versionConcept);
    }

}
