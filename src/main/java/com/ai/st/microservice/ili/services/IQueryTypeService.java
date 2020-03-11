package com.ai.st.microservice.ili.services;

import com.ai.st.microservice.ili.entities.QueryTypeEntity;

public interface IQueryTypeService {

	public Long getCount();

	public QueryTypeEntity createQueryType(QueryTypeEntity queryType);

	public QueryTypeEntity getQueryTypeById(Long id);

}
