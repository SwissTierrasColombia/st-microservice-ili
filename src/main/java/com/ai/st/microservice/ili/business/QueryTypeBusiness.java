package com.ai.st.microservice.ili.business;

import org.springframework.stereotype.Component;

@Component
public class QueryTypeBusiness {

	public static final Long QUERY_TYPE_MATCH_INTEGRATION = (long) 1;
	public static final Long QUERY_TYPE_INSERT_INTEGRATION_ = (long) 2;
	public static final Long QUERY_TYPE_COUNT_SNR_INTEGRATION = (long) 3;
	public static final Long QUERY_TYPE_COUNT_CADASTRE_INTEGRATION = (long) 4;
	public static final Long QUERY_TYPE_COUNT_MATCH_INTEGRATION = (long) 5;
	public static final Long QUERY_TYPE_GET_PAIRING_TYPE_INTEGRATION = (long) 11;

	public static final Long QUERY_TYPE_REGISTRAL_GET_RECORDS_TO_REVISION = (long) 6;
	public static final Long QUERY_TYPE_COUNT_REGISTRAL_GET_RECORDS_TO_REVISION = (long) 7;
	public static final Long QUERY_TYPE_INSERT_EXTARCHIVO_REVISION = (long) 8;
	public static final Long QUERY_TYPE_SELECT_EXTARCHIVO_REVISION = (long) 9;
	public static final Long QUERY_TYPE_UPDATE_EXTARCHIVO_REVISION = (long) 10;

}
