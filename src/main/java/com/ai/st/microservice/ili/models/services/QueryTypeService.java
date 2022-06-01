package com.ai.st.microservice.ili.models.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.entities.QueryTypeEntity;
import com.ai.st.microservice.ili.models.repositories.QueryTypeRepository;

@Service
public class QueryTypeService implements IQueryTypeService {

    @Autowired
    private QueryTypeRepository queryTypeRepository;

    @Override
    public Long getCount() {
        return queryTypeRepository.count();
    }

    @Override
    @Transactional
    public QueryTypeEntity createQueryType(QueryTypeEntity queryType) {
        return queryTypeRepository.save(queryType);
    }

    @Override
    public QueryTypeEntity getQueryTypeById(Long id) {
        return queryTypeRepository.findById(id).orElse(null);
    }

}
