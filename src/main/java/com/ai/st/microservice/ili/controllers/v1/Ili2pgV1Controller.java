package com.ai.st.microservice.ili.controllers.v1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ai.st.microservice.ili.business.ConceptBusiness;
import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.BasicResponseDto;
import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.Ili2pgExportReferenceDto;
import com.ai.st.microservice.ili.dto.Ili2pgImportReferenceDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IliProcessQueueDto;
import com.ai.st.microservice.ili.dto.RequestIli2pgImportDto;
import com.ai.st.microservice.ili.dto.ResponseImportDto;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.exceptions.BusinessException;
import com.ai.st.microservice.ili.exceptions.InputValidationException;
import com.ai.st.microservice.ili.services.Ili2pgService;
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

	@Autowired
	private RabbitMQSenderService rabbitSenderService;

	@Autowired
	private VersionBusiness versionBusiness;

	@Value("${st.temporalDirectory}")
	private String stTemporalDirectory;

	@Value("${iliProcesses.srs}")
	private String srsDefault;

	@RequestMapping(value = "schema-import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Generate database")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Database generated", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> schemaImport(@RequestBody RequestIli2pgImportDto ili2pgImportDto) {

		Ili2pgService ili2pg = new Ili2pgService();

		HttpStatus httpStatus;
		Object responseDto;

		try {

			// validation database host
			String databaseHost = ili2pgImportDto.getDatabaseHost();
			if (databaseHost.isEmpty()) {
				throw new InputValidationException("El host de la base de datos es requerida.");
			}

			// validation database name
			String databaseName = ili2pgImportDto.getDatabaseName();
			if (databaseName.isEmpty()) {
				throw new InputValidationException("El nombre de la base de datos es requerida.");
			}

			// validation database schema
			String databaseSchema = ili2pgImportDto.getDatabaseSchema();
			if (databaseSchema.isEmpty()) {
				throw new InputValidationException("El esquema de la base de datos es requerida.");
			}

			// validation database username
			String databaseUsername = ili2pgImportDto.getDatabaseUsername();
			if (databaseUsername.isEmpty()) {
				throw new InputValidationException("El usuario de base de datos es requerido.");
			}

			// validation database password
			String databasePassword = ili2pgImportDto.getDatabasePassword();
			if (databasePassword.isEmpty()) {
				throw new InputValidationException("La constraseña de la base de datos es requerida.");
			}

			// validation database port
			String databasePort = ili2pgImportDto.getDatabasePort();
			if (databasePort.isEmpty()) {
				throw new InputValidationException("El puerto de base de datos es requerido.");
			}

			VersionDataDto versionData = versionBusiness.getDataVersion(ili2pgImportDto.getVersionModel(),
					ConceptBusiness.CONCEPT_OPERATION);
			if (versionData == null) {
				throw new InputValidationException(
						"No se puede realizar la operación por falta de configuración de los modelos ILI");
			}

			String nameDirectory = "ili_process_" + RandomStringUtils.random(7, false, true);
			Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

			String logFileSchemaImport = tmpDirectory.toString() + File.separator + "schema_import.log";

			Boolean result = ili2pg.generateSchema(logFileSchemaImport, versionData.getUrl(), srsDefault,
					versionData.getModels(), ili2pgImportDto.getDatabaseHost(), ili2pgImportDto.getDatabasePort(),
					ili2pgImportDto.getDatabaseName(), ili2pgImportDto.getDatabaseSchema(),
					ili2pgImportDto.getDatabaseUsername(), ili2pgImportDto.getDatabasePassword());

			if (result) {
				responseDto = new ResponseImportDto(true, "Se ha generado el esquema en la base de datos.");
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
				responseDto = new ResponseImportDto(false, "No se ha podido generar el esquema en la base de datos.");
			}

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@schemaImport#Validation ---> " + e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (BusinessException e) {
			log.error("Error Ili2pgV1Controller@schemaImport#Business ---> " + e.getMessage());
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (IOException e) {
			log.error("Error Ili2pgV1Controller@schemaImport#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

	@RequestMapping(value = "import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = "Generate database and import data from XTF file")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Database generated and data imported", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> importXtf(@ModelAttribute RequestIli2pgImportDto ili2pgImportDto) {

		Ili2pgService ili2pg = new Ili2pgService();

		HttpStatus httpStatus = null;
		Object responseDto = null;

		try {

			// validation database host
			String databaseHost = ili2pgImportDto.getDatabaseHost();
			if (databaseHost.isEmpty()) {
				throw new InputValidationException("El host de la base de datos es requerida.");
			}

			// validation database name
			String databaseName = ili2pgImportDto.getDatabaseName();
			if (databaseName.isEmpty()) {
				throw new InputValidationException("El nombre de la base de datos es requerida.");
			}

			// validation database schema
			String databaseSchema = ili2pgImportDto.getDatabaseSchema();
			if (databaseSchema.isEmpty()) {
				throw new InputValidationException("El esquema de la base de datos es requerida.");
			}

			// validation database username
			String databaseUsername = ili2pgImportDto.getDatabaseUsername();
			if (databaseUsername.isEmpty()) {
				throw new InputValidationException("El usuario de base de datos es requerido.");
			}

			// validation database password
			String databasePassword = ili2pgImportDto.getDatabasePassword();
			if (databasePassword.isEmpty()) {
				throw new InputValidationException("La constraseña de la base de datos es requerida.");
			}

			// validation database port
			String databasePort = ili2pgImportDto.getDatabasePort();
			if (databasePort.isEmpty()) {
				throw new InputValidationException("El puerto de base de datos es requerido.");
			}

			VersionDataDto versionData = versionBusiness.getDataVersion(ili2pgImportDto.getVersionModel(),
					ConceptBusiness.CONCEPT_OPERATION);
			if (versionData == null) {
				throw new InputValidationException(
						"No se puede realizar la operación por falta de configuración de los modelos ILI");
			}

			MultipartFile uploadFile = ili2pgImportDto.getFileXTF();

			String nameDirectory = "ili_process_" + RandomStringUtils.random(7, false, true);
			Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

			String filename = uploadFile.getOriginalFilename();
			String filepath = Paths.get(tmpDirectory.toString(), filename).toString();

			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)))) {
				stream.write(uploadFile.getBytes());
			}

			String logFileSchemaImport = tmpDirectory.toString() + File.separator + "schema_import.log";
			String logFileImport = tmpDirectory.toString() + File.separator + "import.log";

			Boolean result = ili2pg.import2pg(filepath, logFileSchemaImport, logFileImport, versionData.getUrl(),
					srsDefault, versionData.getModels(), ili2pgImportDto.getDatabaseHost(),
					ili2pgImportDto.getDatabasePort(), ili2pgImportDto.getDatabaseName(),
					ili2pgImportDto.getDatabaseSchema(), ili2pgImportDto.getDatabaseUsername(),
					ili2pgImportDto.getDatabasePassword());

			if (result) {
				responseDto = new ResponseImportDto(true, "Se ha importado la información.");
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
				responseDto = new ResponseImportDto(false, "No se ha podido importar la información.");
			}

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@importXtf#Validation ---> " + e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (BusinessException e) {
			log.error("Error Ili2pgV1Controller@importXtf#Business ---> " + e.getMessage());
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (IOException e) {
			log.error("Error Ili2pgV1Controller@importXtf#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

	@RequestMapping(value = "integration/cadastre-registration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = "Integration Cadastre-Registration")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> integrationCadestreRegistrationWithFiles(
			@ModelAttribute Ili2pgIntegrationCadastreRegistrationDto requestIntegrationDto) {

		Ili2pgService ili2pg = new Ili2pgService();

		HttpStatus httpStatus;
		Object responseDto;

		try {

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
				throw new InputValidationException("La constraseña de la base de datos es requerida.");
			}

			// validation database port
			String databasePort = requestIntegrationDto.getDatabasePort();
			if (databasePort.isEmpty()) {
				throw new InputValidationException("El puerto de base de datos es requerido.");
			}

			String nameDirectory = "ili_process_" + RandomStringUtils.random(7, false, true);
			Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

			// upload cadastre file
			MultipartFile uploadFileCadastre = requestIntegrationDto.getCadastreFileXTF();

			String cadastreFilename = uploadFileCadastre.getOriginalFilename();
			String cadastreFilepath = Paths.get(tmpDirectory.toString(), cadastreFilename).toString();
			try (BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(cadastreFilepath)))) {
				stream.write(uploadFileCadastre.getBytes());
			}

			VersionDataDto versionData = versionBusiness.getDataVersion(requestIntegrationDto.getVersionModel(),
					ConceptBusiness.CONCEPT_INTEGRATION);
			if (versionData == null) {
				throw new InputValidationException(
						"No se puede realizar la operación por falta de configuración de los modelos ILI");
			}

			// upload registration file
			MultipartFile uploadFileRegistration = requestIntegrationDto.getRegistrationFileXTF();
			String registrationFilename = uploadFileRegistration.getOriginalFilename();
			String registrationFilepath = Paths.get(tmpDirectory.toString(), registrationFilename).toString();
			try (BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(registrationFilepath))) {
				stream.write(uploadFileRegistration.getBytes());
			}

			String cadastreLogFileSchemaImport = tmpDirectory.toString() + File.separator
					+ "cadastre_schema_import.log";
			String cadastreLogFileImport = tmpDirectory.toString() + File.separator + "cadastre_import.log";

			String registrationLogFileSchemaImport = tmpDirectory.toString() + File.separator
					+ "registration_schema_import.log";
			String registrationLogFileImport = tmpDirectory.toString() + File.separator + "registration_import.log";

			responseDto = ili2pg.integration(cadastreFilepath, cadastreLogFileSchemaImport, cadastreLogFileImport,
					registrationFilepath, registrationLogFileSchemaImport, registrationLogFileImport,
					versionData.getUrl(), srsDefault, versionData.getModels(), requestIntegrationDto.getDatabaseHost(),
					requestIntegrationDto.getDatabasePort(), requestIntegrationDto.getDatabaseName(),
					requestIntegrationDto.getDatabaseSchema(), requestIntegrationDto.getDatabaseUsername(),
					requestIntegrationDto.getDatabasePassword(), requestIntegrationDto.getVersionModel());

			httpStatus = HttpStatus.OK;

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@integrationCadestreRegistrationWithFiles#Validation ---> "
					+ e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (BusinessException e) {
			log.error("Error Ili2pgV1Controller@integrationCadestreRegistrationWithFiles#Business ---> "
					+ e.getMessage());
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (IOException e) {
			log.error(
					"Error Ili2pgV1Controller@integrationCadestreRegistrationWithFiles#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

	@RequestMapping(value = "integration/cadastre-registration-reference", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Integration Cadastre-Registration")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> integrationCadestreRegistrationWithoutFiles(
			@RequestBody Ili2pgIntegrationCadastreRegistrationWithoutFilesDto requestIntegrationDto) {

		HttpStatus httpStatus;
		Object responseDto;

		try {

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
				throw new InputValidationException("La constraseña de la base de datos es requerida.");
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
			responseDto = new BasicResponseDto("Integración iniciada!", 7);

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@integrationCadestreRegistrationWithoutFiles#Validation ---> "
					+ e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (BusinessException e) {
			log.error("Error Ili2pgV1Controller@integrationCadestreRegistrationWithoutFiles#Business ---> "
					+ e.getMessage());
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (Exception e) {
			log.error("Error Ili2pgV1Controller@integrationCadestreRegistrationWithoutFiles#General ---> "
					+ e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

	@RequestMapping(value = "export", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Export ")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> exportToXtf(@RequestBody Ili2pgExportDto requestExportDto) {

		HttpStatus httpStatus;
		Object responseDto;

		try {

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
			responseDto = new BasicResponseDto("¡Export started!", 5);

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#Validation ---> " + e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (BusinessException e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#Business ---> " + e.getMessage());
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (Exception e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

	@RequestMapping(value = "import-reference", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Import xtf to database with ili2pg")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Import started", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> importPerReference(@RequestBody Ili2pgImportReferenceDto importReferenceDto) {

		HttpStatus httpStatus;
		Object responseDto;

		try {

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
				throw new InputValidationException("La constraseña de la base de datos es requerida.");
			}

			// validation database port
			String databasePort = importReferenceDto.getDatabasePort();
			if (databasePort.isEmpty()) {
				throw new InputValidationException("El puerto de base de datos es requerido.");
			}

			// validation version model
			String versionModel = importReferenceDto.getVersionModel();
			if (databasePort.isEmpty()) {
				throw new InputValidationException("La version del modelo LADM COL es requerida.");
			}

			// validation concept
			Long conceptId = importReferenceDto.getConceptId();
			if (conceptId == null) {
				throw new InputValidationException("El concepto es requerido.");
			}

			VersionDataDto versionData = versionBusiness.getDataVersion(versionModel, conceptId);
			if (!(versionData instanceof VersionDataDto)) {
				throw new InputValidationException(
						"No se puede realizar la operación por falta de configuración de los modelos ILI");
			}

			IliProcessQueueDto data = new IliProcessQueueDto();
			data.setType(IliProcessQueueDto.IMPORT_REFERENCE);
			data.setImportReferenceData(importReferenceDto);
			rabbitSenderService.sendDataToIliProcess(data);

			httpStatus = HttpStatus.OK;
			responseDto = new BasicResponseDto("Proceso iniciado!", 7);

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@importPerReference#Validation ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (BusinessException e) {
			log.error("Error Ili2pgV1Controller@importPerReference#Business ---> " + e.getMessage());
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (Exception e) {
			log.error("Error Ili2pgV1Controller@importPerReference#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

	@RequestMapping(value = "export-reference", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Export ")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> exportReference(@RequestBody Ili2pgExportReferenceDto requestExportDto) {

		HttpStatus httpStatus = null;
		Object responseDto = null;

		try {

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
			if (databasePort.isEmpty()) {
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
			responseDto = new BasicResponseDto("¡Export started!", 5);

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#Validation ---> " + e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (BusinessException e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#Business ---> " + e.getMessage());
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (Exception e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

}
