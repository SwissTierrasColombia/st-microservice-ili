package com.ai.st.microservice.ili.controllers.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.BasicResponseDto;
import com.ai.st.microservice.ili.dto.VersionDto;
import com.ai.st.microservice.ili.exceptions.BusinessException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Versions", description = "Manage Versions", tags = { "versions" })
@RestController
@RequestMapping("api/ili/versions/v1/versions")
public class VersionV1Controller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private VersionBusiness versionBusiness;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get model versions")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Get versions", response = VersionDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class) })
    @ResponseBody
    public ResponseEntity<?> getVersions() {

        HttpStatus httpStatus = null;
        Object responseDto = null;

        try {

            responseDto = versionBusiness.getAvailableVersions();
            httpStatus = HttpStatus.OK;

        } catch (BusinessException e) {
            log.error("Error VersionV1Controller@getVersions#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        } catch (Exception e) {
            log.error("Error VersionV1Controller@getVersions#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

}
