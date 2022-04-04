package com.ai.st.microservice.ili.models.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ai.st.microservice.ili.entities.VersionEntity;

public interface VersionRepository extends CrudRepository<VersionEntity, Long> {

    VersionEntity findByName(String name);

    @Override
    List<VersionEntity> findAll();

}
