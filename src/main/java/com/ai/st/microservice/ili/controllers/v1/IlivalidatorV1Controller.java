package com.ai.st.microservice.ili.controllers.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.BasicResponseDto;
import com.ai.st.microservice.ili.dto.IliProcessQueueDto;
import com.ai.st.microservice.ili.dto.IlivalidatorBackgroundDto;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.exceptions.BusinessException;
import com.ai.st.microservice.ili.exceptions.InputValidationException;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Ilivalidator", tags = {"ilivalidator"})
@RestController
@RequestMapping("api/ili/ilivalidator/v1")
public class IlivalidatorV1Controller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RabbitMQSenderService rabbitSenderService;
    private final VersionBusiness versionBusiness;

    public IlivalidatorV1Controller(RabbitMQSenderService rabbitSenderService, VersionBusiness versionBusiness) {
        this.rabbitSenderService = rabbitSenderService;
        this.versionBusiness = versionBusiness;
    }

    @RequestMapping(value = "validate/background", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Export ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Validation done"),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<?> validateBackground(@RequestBody IlivalidatorBackgroundDto request) {

        HttpStatus httpStatus;
        Object responseDto;

        try {

            // validation path file
            String pathFile = request.getPathFile();
            if (pathFile.isEmpty()) {
                throw new InputValidationException("La ruta del archivo a generar es requerida.");
            }

            VersionDataDto versionData = versionBusiness.getDataVersion(request.getVersionModel(),
                    request.getConceptId());
            if (versionData == null) {
                throw new InputValidationException(
                        "No se puede realizar la operación por falta de configuración de los modelos ILI");
            }

            IliProcessQueueDto data = new IliProcessQueueDto();
            data.setType(IliProcessQueueDto.VALIDATOR);
            data.setIlivalidatorData(request);

            rabbitSenderService.sendDataToIliProcess(data);

            httpStatus = HttpStatus.OK;
            responseDto = new BasicResponseDto("¡Validación iniciada!", 5);

        } catch (InputValidationException e) {
            log.error("Error IlivalidatorV1Controller@validateBackground#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        } catch (BusinessException e) {
            log.error("Error IlivalidatorV1Controller@validateBackground#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        } catch (Exception e) {
            log.error("Error IlivalidatorV1Controller@validateBackground#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

}
