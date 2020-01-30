package com.ai.st.microservice.ili.repositories;

import org.springframework.data.repository.CrudRepository;

import com.ai.st.microservice.ili.entities.VersionEntity;

public interface VersionRepository extends CrudRepository<VersionEntity, Long> {

	VersionEntity findByName(String name);

}
