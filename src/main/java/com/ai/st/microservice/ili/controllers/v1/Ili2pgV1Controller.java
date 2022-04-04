package com.ai.st.microservice.ili.controllers.v1;

import com.ai.st.microservice.common.dto.general.BasicResponseDto;
import com.ai.st.microservice.ili.services.tracing.SCMTracing;
import com.ai.st.microservice.ili.services.tracing.TracingKeyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ai.st.microservice.ili.business.ConceptBusiness;
import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.Ili2pgExportReferenceDto;
import com.ai.st.microservice.ili.dto.Ili2pgImportReferenceDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IliProcessQueueDto;
import com.ai.st.microservice.ili.dto.ResponseImportDto;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.exceptions.BusinessException;
import com.ai.st.microservice.ili.exceptions.InputValidationException;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Ili2pg", tags = { "ili2pg" })
@RestController
@RequestMapping("api/ili/ili2pg/v1")
public class Ili2pgV1Controller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${st.temporalDirectory}")
    private String stTemporalDirectory;

    @Value("${iliProcesses.srs}")
    private String srsDefault;

    private final RabbitMQSenderService rabbitSenderService;
    private final VersionBusiness versionBusiness;

    public Ili2pgV1Controller(RabbitMQSenderService rabbitSenderService, VersionBusiness versionBusiness) {
        this.rabbitSenderService = rabbitSenderService;
        this.versionBusiness = versionBusiness;
    }

    @PostMapping(value = "integration/cadastre-registration-reference", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Integration Cadastre-Registration")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class) })
    @ResponseBody
    public ResponseEntity<?> makeIntegrationCadastralRegistral(
            @RequestBody Ili2pgIntegrationCadastreRegistrationWithoutFilesDto requestIntegrationDto) {

        HttpStatus httpStatus;
        Object responseDto;

        try {

            SCMTracing.setTransactionName("makeIntegrationCadastralRegistral");
            SCMTracing.addCustomParameter(TracingKeyword.BODY_REQUEST, requestIntegrationDto.toString());

            // validation cadastre file
            String cadastreFile = requestIntegrationDto.getCadastrePathXTF();
            if (cadastreFile.isEmpty()) {
                throw new InputValidationException("El archivo de catastro es requerido para la integración.");
            }

            // validation registration file
            String registrationFile = requestIntegrationDto.getRegistrationPathXTF();
            if (registrationFile.isEmpty()) {
                throw new InputValidationException("El archivo de registro es requerido para la integración.");
            }

            // validation database host
            String databaseHost = requestIntegrationDto.getDatabaseHost();
            if (databaseHost.isEmpty()) {
                throw new InputValidationException("El host de la base de datos es requerida.");
            }

            // validation database name
            String databaseName = requestIntegrationDto.getDatabaseName();
            if (databaseName.isEmpty()) {
                throw new InputValidationException("El nombre de la base de datos es requerida.");
            }

            // validation database schema
            String databaseSchema = requestIntegrationDto.getDatabaseSchema();
            if (databaseSchema.isEmpty()) {
                throw new InputValidationException("El esquema de la base de datos es requerida.");
            }

            // validation database username
            String databaseUsername = requestIntegrationDto.getDatabaseUsername();
            if (databaseUsername.isEmpty()) {
                throw new InputValidationException("El usuario de base de datos es requerido.");
            }

            // validation database password
            String databasePassword = requestIntegrationDto.getDatabasePassword();
            if (databasePassword.isEmpty()) {
                throw new InputValidationException("La contraseña de la base de datos es requerida.");
            }

            // validation database port
            String databasePort = requestIntegrationDto.getDatabasePort();
            if (databasePort.isEmpty()) {
                throw new InputValidationException("El puerto de base de datos es requerido.");
            }

            VersionDataDto versionData = versionBusiness.getDataVersion(requestIntegrationDto.getVersionModel(),
                    ConceptBusiness.CONCEPT_INTEGRATION);
            if (versionData == null) {
                throw new InputValidationException(
                        "No se puede realizar la operación por falta de configuración de los modelos ILI");
            }

            IliProcessQueueDto data = new IliProcessQueueDto();
            data.setType(IliProcessQueueDto.INTEGRATOR);
            data.setIntegrationData(requestIntegrationDto);

            rabbitSenderService.sendDataToIliProcess(data);

            httpStatus = HttpStatus.OK;
            responseDto = new BasicResponseDto("Integración iniciada!");

        } catch (InputValidationException e) {
            log.error("Error Ili2pgV1Controller@makeIntegrationCadastralRegistral#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (BusinessException e) {
            log.error("Error Ili2pgV1Controller@makeIntegrationCadastralRegistral#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (Exception e) {
            log.error("Error Ili2pgV1Controller@makeIntegrationCadastralRegistral#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PostMapping(value = "export", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Export ")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class) })
    @ResponseBody
    public ResponseEntity<?> exportToXtf(@RequestBody Ili2pgExportDto requestExportDto) {

        HttpStatus httpStatus;
        Object responseDto;

        try {

            SCMTracing.setTransactionName("exportToXtf");
            SCMTracing.addCustomParameter(TracingKeyword.BODY_REQUEST, requestExportDto.toString());

            // validation path file
            String pathFile = requestExportDto.getPathFileXTF();
            if (pathFile.isEmpty()) {
                throw new InputValidationException("La ruta del archivo a generar es requerida.");
            }

            // validation database host
            String databaseHost = requestExportDto.getDatabaseHost();
            if (databaseHost.isEmpty()) {
                throw new InputValidationException("El host de la base de datos es requerida.");
            }

            // validation database name
            String databaseName = requestExportDto.getDatabaseName();
            if (databaseName.isEmpty()) {
                throw new InputValidationException("El nombre de la base de datos es requerida.");
            }

            // validation database schema
            String databaseSchema = requestExportDto.getDatabaseSchema();
            if (databaseSchema.isEmpty()) {
                throw new InputValidationException("El esquema de la base de datos es requerida.");
            }

            // validation database username
            String databaseUsername = requestExportDto.getDatabaseUsername();
            if (databaseUsername.isEmpty()) {
                throw new InputValidationException("El usuario de base de datos es requerido.");
            }

            // validation database password
            String databasePassword = requestExportDto.getDatabasePassword();
            if (databasePassword.isEmpty()) {
                throw new InputValidationException("La contraseña de la base de datos es requerida.");
            }

            // validation database port
            String databasePort = requestExportDto.getDatabasePort();
            if (databasePort.isEmpty()) {
                throw new InputValidationException("El puerto de base de datos es requerido.");
            }

            // validation with stats
            Boolean requiredStats = requestExportDto.getWithStats();
            if (requiredStats == null) {
                throw new InputValidationException("Se debe especificar si se requieren estadísticas.");
            }

            VersionDataDto versionData = versionBusiness.getDataVersion(requestExportDto.getVersionModel(),
                    ConceptBusiness.CONCEPT_INTEGRATION);
            if (versionData == null) {
                throw new InputValidationException(
                        "No se puede realizar la operación por falta de configuración de los modelos ILI");
            }

            IliProcessQueueDto data = new IliProcessQueueDto();
            data.setType(IliProcessQueueDto.EXPORT);
            data.setExportData(requestExportDto);

            rabbitSenderService.sendDataToIliProcess(data);

            httpStatus = HttpStatus.OK;
            responseDto = new BasicResponseDto("¡Export started!");

        } catch (InputValidationException e) {
            log.error("Error Ili2pgV1Controller@exportToXtf#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (BusinessException e) {
            log.error("Error Ili2pgV1Controller@exportToXtf#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (Exception e) {
            log.error("Error Ili2pgV1Controller@exportToXtf#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PostMapping(value = "import-reference", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Import xtf to database with ili2pg")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Import started", response = ResponseImportDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class) })
    @ResponseBody
    public ResponseEntity<?> importByReference(@RequestBody Ili2pgImportReferenceDto importReferenceDto) {

        HttpStatus httpStatus;
        Object responseDto;

        try {

            SCMTracing.setTransactionName("importByReference");
            SCMTracing.addCustomParameter(TracingKeyword.BODY_REQUEST, importReferenceDto.toString());

            // validation file
            String pathFile = importReferenceDto.getPathXTF();
            if (pathFile.isEmpty()) {
                throw new InputValidationException("El archivo es requerido.");
            }

            // validation database host
            String databaseHost = importReferenceDto.getDatabaseHost();
            if (databaseHost.isEmpty()) {
                throw new InputValidationException("El host de la base de datos es requerida.");
            }

            // validation database name
            String databaseName = importReferenceDto.getDatabaseName();
            if (databaseName.isEmpty()) {
                throw new InputValidationException("El nombre de la base de datos es requerida.");
            }

            // validation database schema
            String databaseSchema = importReferenceDto.getDatabaseSchema();
            if (databaseSchema.isEmpty()) {
                throw new InputValidationException("El esquema de la base de datos es requerida.");
            }

            // validation database username
            String databaseUsername = importReferenceDto.getDatabaseUsername();
            if (databaseUsername.isEmpty()) {
                throw new InputValidationException("El usuario de base de datos es requerido.");
            }

            // validation database password
            String databasePassword = importReferenceDto.getDatabasePassword();
            if (databasePassword.isEmpty()) {
                throw new InputValidationException("La contraseña de la base de datos es requerida.");
            }

            // validation database port
            String databasePort = importReferenceDto.getDatabasePort();
            if (databasePort.isEmpty()) {
                throw new InputValidationException("El puerto de base de datos es requerido.");
            }

            // validation version model
            String versionModel = importReferenceDto.getVersionModel();
            if (versionModel.isEmpty()) {
                throw new InputValidationException("La version del modelo LADM COL es requerida.");
            }

            // validation concept
            Long conceptId = importReferenceDto.getConceptId();
            if (conceptId == null) {
                throw new InputValidationException("El concepto es requerido.");
            }

            VersionDataDto versionData = versionBusiness.getDataVersion(versionModel, conceptId);
            if (versionData == null) {
                throw new InputValidationException(
                        "No se puede realizar la operación por falta de configuración de los modelos ILI");
            }

            IliProcessQueueDto data = new IliProcessQueueDto();
            data.setType(IliProcessQueueDto.IMPORT_REFERENCE);
            data.setImportReferenceData(importReferenceDto);
            rabbitSenderService.sendDataToIliProcess(data);

            httpStatus = HttpStatus.OK;
            responseDto = new BasicResponseDto("Proceso iniciado!");

        } catch (InputValidationException e) {
            log.error("Error Ili2pgV1Controller@importByReference#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (BusinessException e) {
            log.error("Error Ili2pgV1Controller@importByReference#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (Exception e) {
            log.error("Error Ili2pgV1Controller@importByReference#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PostMapping(value = "export-reference", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Export ")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class) })
    @ResponseBody
    public ResponseEntity<?> exportByReference(@RequestBody Ili2pgExportReferenceDto requestExportDto) {

        HttpStatus httpStatus;
        Object responseDto;

        try {

            SCMTracing.setTransactionName("exportByReference");
            SCMTracing.addCustomParameter(TracingKeyword.BODY_REQUEST, requestExportDto.toString());

            // validation path file
            String pathFile = requestExportDto.getPathFileXTF();
            if (pathFile.isEmpty()) {
                throw new InputValidationException("La ruta del archivo a generar es requerida.");
            }

            // validation database host
            String databaseHost = requestExportDto.getDatabaseHost();
            if (databaseHost.isEmpty()) {
                throw new InputValidationException("El host de la base de datos es requerida.");
            }

            // validation database name
            String databaseName = requestExportDto.getDatabaseName();
            if (databaseName.isEmpty()) {
                throw new InputValidationException("El nombre de la base de datos es requerida.");
            }

            // validation database schema
            String databaseSchema = requestExportDto.getDatabaseSchema();
            if (databaseSchema.isEmpty()) {
                throw new InputValidationException("El esquema de la base de datos es requerida.");
            }

            // validation database username
            String databaseUsername = requestExportDto.getDatabaseUsername();
            if (databaseUsername.isEmpty()) {
                throw new InputValidationException("El usuario de base de datos es requerido.");
            }

            // validation database password
            String databasePassword = requestExportDto.getDatabasePassword();
            if (databasePassword.isEmpty()) {
                throw new InputValidationException("La constraseña de la base de datos es requerida.");
            }

            // validation database port
            String databasePort = requestExportDto.getDatabasePort();
            if (databasePort.isEmpty()) {
                throw new InputValidationException("El puerto de base de datos es requerido.");
            }

            // validation version model
            String versionModel = requestExportDto.getVersionModel();
            if (versionModel.isEmpty()) {
                throw new InputValidationException("La version del modelo LADM COL es requerida.");
            }

            // validation concept
            Long conceptId = requestExportDto.getConceptId();
            if (conceptId == null) {
                throw new InputValidationException("El concepto es requerido.");
            }

            VersionDataDto versionData = versionBusiness.getDataVersion(requestExportDto.getVersionModel(), conceptId);
            if (versionData == null) {
                throw new InputValidationException(
                        "No se puede realizar la operación por falta de configuración de los modelos ILI");
            }

            IliProcessQueueDto data = new IliProcessQueueDto();
            data.setType(IliProcessQueueDto.EXPORT_REFERENCE);
            data.setExportReferenceData(requestExportDto);

            rabbitSenderService.sendDataToIliProcess(data);

            httpStatus = HttpStatus.OK;
            responseDto = new BasicResponseDto("¡Export started!");

        } catch (InputValidationException e) {
            log.error("Error Ili2pgV1Controller@exportByReference#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (BusinessException e) {
            log.error("Error Ili2pgV1Controller@exportByReference#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        } catch (Exception e) {
            log.error("Error Ili2pgV1Controller@exportByReference#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage());
            SCMTracing.sendError(e.getMessage());
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

}
