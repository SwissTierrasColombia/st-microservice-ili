package com.ai.st.microservice.ili.business;

import org.springframework.stereotype.Component;

@Component
public class QueryTypeBusiness {

	public static final Long QUERY_TYPE_MATCH_INTEGRATION = (long) 1;
	public static final Long QUERY_TYPE_INSERT_INTEGRATION_ = (long) 2;
	public static final Long QUERY_TYPE_COUNT_SNR_INTEGRATION = (long) 3;
	public static final Long QUERY_TYPE_COUNT_CADASTRE_INTEGRATION = (long) 4;
	public static final Long QUERY_TYPE_COUNT_MATCH_INTEGRATION = (long) 5;
	
	public static final Long QUERY_TYPE_REGISTRAL_GET_RECORDS_TO_REVISION = (long) 6;
	public static final Long QUERY_TYPE_COUNT_REGISTRAL_GET_RECORDS_TO_REVISION = (long) 7;

}
