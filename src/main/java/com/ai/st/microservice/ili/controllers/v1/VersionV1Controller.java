package com.ai.st.microservice.ili.controllers.v1;

import com.ai.st.microservice.common.dto.general.BasicResponseDto;
import com.ai.st.microservice.ili.services.tracing.SCMTracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.VersionDto;
import com.ai.st.microservice.ili.exceptions.BusinessException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Versions", tags = { "versions" })
@RestController
@RequestMapping("api/ili/versions/v1/versions")
public class VersionV1Controller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final VersionBusiness versionBusiness;

    public VersionV1Controller(VersionBusiness versionBusiness) {
        this.versionBusiness = versionBusiness;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get model versions")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Get versions", response = VersionDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class) })
    @ResponseBody
    public ResponseEntity<?> getVersions() {

        HttpStatus httpStatus;
        Object responseDto;

        try {

            SCMTracing.setTransactionName("getVersions");

            responseDto = versionBusiness.getAvailableVersions();
            httpStatus = HttpStatus.OK;

        } catch (BusinessException e) {
            log.error("Error VersionV1Controller@getVersions#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (Exception e) {
            log.error("Error VersionV1Controller@getVersions#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

}
