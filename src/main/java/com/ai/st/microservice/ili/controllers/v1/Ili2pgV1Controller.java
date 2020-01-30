package com.ai.st.microservice.ili.controllers.v1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.BasicResponseDto;
import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
import com.ai.st.microservice.ili.dto.RequestIli2pgImportDto;
import com.ai.st.microservice.ili.dto.ResponseImportDto;
import com.ai.st.microservice.ili.exceptions.BusinessException;
import com.ai.st.microservice.ili.exceptions.InputValidationException;
import com.ai.st.microservice.ili.services.Ili2pgService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Ili2pg", description = "Import and export file XTF to databases posgresql", tags = { "ili2pg" })
@RestController
@RequestMapping("api/ili/ili2pg/v1")
public class Ili2pgV1Controller {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RabbitMQSenderService rabbitSenderService;

	@Autowired
	private VersionBusiness versionBusiness;

	@Value("${iliProcesses.temporalDirectoryPrefix}")
	private String temporalDirectoryPrefix;

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

	@Value("${iliProcesses.iliDirectory}")
	private String iliDirectory;

	@Value("${iliProcesses.models}")
	private String modelsDefault;

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

			versionBusiness.getVersionByName(ili2pgImportDto.getVersionModel());

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			String logFileSchemaImport = tmpDirectory.toString() + File.separator + "schema_import.log";

			Boolean result = ili2pg.generateSchema(logFileSchemaImport, iliDirectory, srsDefault, modelsDefault,
					ili2pgImportDto.getDatabaseHost(), ili2pgImportDto.getDatabasePort(),
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
			log.error("Error Ili2pgV1Controller@schemaImport#Validation ---> " + e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
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
	public ResponseEntity<ResponseImportDto> importXtf(@ModelAttribute RequestIli2pgImportDto ili2pgImportDto) {

		Ili2pgService ili2pg = new Ili2pgService();

		Boolean result = false;
		String message = "";

		try {

			MultipartFile uploadFile = ili2pgImportDto.getFileXTF();

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			String filename = uploadFile.getOriginalFilename();
			String filepath = Paths.get(tmpDirectory.toString(), filename).toString();

			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)))) {
				stream.write(uploadFile.getBytes());
			}

			String logFileSchemaImport = tmpDirectory.toString() + File.separator + "schema_import.log";
			String logFileImport = tmpDirectory.toString() + File.separator + "import.log";

			result = ili2pg.import2pg(filepath, logFileSchemaImport, logFileImport, iliDirectory, srsDefault,
					modelsDefault, ili2pgImportDto.getDatabaseHost(), ili2pgImportDto.getDatabasePort(),
					ili2pgImportDto.getDatabaseName(), ili2pgImportDto.getDatabaseSchema(),
					ili2pgImportDto.getDatabaseUsername(), ili2pgImportDto.getDatabasePassword());

			message = (result) ? "Information imported" : "The process could not be performed";

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return new ResponseEntity<>(new ResponseImportDto(result, message), HttpStatus.OK);
	}

	@RequestMapping(value = "integration/cadastre-registration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = "Integration Cadastre-Registration")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<IntegrationStatDto> integrationCadestreRegistrationWithFiles(
			@ModelAttribute Ili2pgIntegrationCadastreRegistrationDto requestIntegrationDto) {

		Ili2pgService ili2pg = new Ili2pgService();

		IntegrationStatDto integrationStatDto = null;

		try {

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			// upload cadastre file
			MultipartFile uploadFileCadastre = requestIntegrationDto.getCadastreFileXTF();

			String cadastreFilename = uploadFileCadastre.getOriginalFilename();
			String cadastreFilepath = Paths.get(tmpDirectory.toString(), cadastreFilename).toString();
			try (BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(cadastreFilepath)))) {
				stream.write(uploadFileCadastre.getBytes());
			}

			// upload registration file
			MultipartFile uploadFileRegistration = requestIntegrationDto.getRegistrationFileXTF();
			String registrationFilename = uploadFileRegistration.getOriginalFilename();
			String registrationFilepath = Paths.get(tmpDirectory.toString(), registrationFilename).toString();
			try (BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(registrationFilepath)))) {
				stream.write(uploadFileRegistration.getBytes());
			}

			String cadastreLogFileSchemaImport = tmpDirectory.toString() + File.separator
					+ "cadastre_schema_import.log";
			String cadastreLogFileImport = tmpDirectory.toString() + File.separator + "cadastre_import.log";

			String registrationLogFileSchemaImport = tmpDirectory.toString() + File.separator
					+ "registration_schema_import.log";
			String registrationLogFileImport = tmpDirectory.toString() + File.separator + "registration_import.log";

			integrationStatDto = ili2pg.integration(cadastreFilepath, cadastreLogFileSchemaImport,
					cadastreLogFileImport, registrationFilepath, registrationLogFileSchemaImport,
					registrationLogFileImport, iliDirectory, srsDefault, modelsDefault,
					requestIntegrationDto.getDatabaseHost(), requestIntegrationDto.getDatabasePort(),
					requestIntegrationDto.getDatabaseName(), requestIntegrationDto.getDatabaseSchema(),
					requestIntegrationDto.getDatabaseUsername(), requestIntegrationDto.getDatabasePassword());

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return new ResponseEntity<>(integrationStatDto, HttpStatus.OK);
	}

	@RequestMapping(value = "integration/cadastre-registration-reference", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Integration Cadastre-Registration")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> integrationCadestreRegistrationWithoutFiles(
			@RequestBody Ili2pgIntegrationCadastreRegistrationWithoutFilesDto requestIntegrationDto) {

		HttpStatus httpStatus = null;
		Object responseDto = null;

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

			rabbitSenderService.sendDataToIntegrate(requestIntegrationDto);

			httpStatus = HttpStatus.OK;
			responseDto = new BasicResponseDto("Integration started!", 7);

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@integrationCadestreRegistrationWithoutFiles#Validation ---> "
					+ e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
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

			rabbitSenderService.sendDataToExport(requestExportDto);

			httpStatus = HttpStatus.OK;
			responseDto = new BasicResponseDto("¡Export started!", 5);

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#Validation ---> "
					+ e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (Exception e) {
			log.error("Error Ili2pgV1Controller@exportToXtf#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);

	}

}
