package com.ai.st.microservice.ili.controllers.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ai.st.microservice.ili.business.QueryBusiness;
import com.ai.st.microservice.ili.dto.BasicResponseDto;
import com.ai.st.microservice.ili.dto.ExecuteQueryUpdateToRevisionDto;
import com.ai.st.microservice.ili.dto.QueryResultRegistralRevisionDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Query", tags = { "query" })
@RestController
@RequestMapping("api/ili/query/v1")
public class QueryV1Controller {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private QueryBusiness queryBusiness;

	@RequestMapping(value = "execute/registral-to-revision", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Execute query")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Execute query", response = QueryResultRegistralRevisionDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> executeQueryRegistralToRevision(@RequestParam(name = "host") String host,
			@RequestParam(name = "database") String database, @RequestParam(name = "schema") String schema,
			@RequestParam(name = "port") String port, @RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password, @RequestParam(name = "modelVersion") String modelVersion,
			@RequestParam(name = "concept") Long conceptId, @RequestParam(name = "page") int page,
			@RequestParam(name = "limit") int limit) {

		HttpStatus httpStatus = null;
		Object responseDto = null;

		try {

			responseDto = queryBusiness.executeQueryRegistralToRevision(modelVersion, conceptId, host, database, port,
					schema, username, password, page, limit);
			httpStatus = HttpStatus.OK;

		} catch (Exception e) {
			log.error("Error QueryV1Controller@executeQueryRegistralToRevision#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

	@RequestMapping(value = "execute/update-to-revision", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Execute query")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Execute query", response = QueryResultRegistralRevisionDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> executeQueryUpdateToRevision(@RequestBody ExecuteQueryUpdateToRevisionDto executeDto) {

		HttpStatus httpStatus = null;
		Object responseDto = null;

		try {

			queryBusiness.executeQueryUpdateToRevision(executeDto.getVersionModel(), executeDto.getConceptId(),
					executeDto.getDatabaseHost(), executeDto.getDatabaseName(), executeDto.getDatabasePort(),
					executeDto.getDatabaseSchema(), executeDto.getDatabaseUsername(), executeDto.getDatabasePassword(),
					executeDto.getNamespace(), executeDto.getUrlFile(), executeDto.getEntityId(),
					executeDto.getBoundarySpaceId());
			responseDto = new BasicResponseDto("Registro actualizado", 7);
			httpStatus = HttpStatus.OK;

		} catch (Exception e) {
			log.error("Error QueryV1Controller@executeQueryUpdateToRevision#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

}
