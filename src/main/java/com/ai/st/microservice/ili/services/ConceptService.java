package com.ai.st.microservice.ili.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.entities.ConceptEntity;
import com.ai.st.microservice.ili.repositories.ConceptRepository;

@Service
public class ConceptService implements IConceptService {

    @Autowired
    private ConceptRepository conceptRepository;

    @Override
    public Long getCount() {
        return conceptRepository.count();
    }

    @Override
    @Transactional
    public ConceptEntity createConcept(ConceptEntity conceptEntity) {
        return conceptRepository.save(conceptEntity);
    }

    @Override
    public ConceptEntity getConceptById(Long id) {
        return conceptRepository.findById(id).orElse(null);
    }

}
