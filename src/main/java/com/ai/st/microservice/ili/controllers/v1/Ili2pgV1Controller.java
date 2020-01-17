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

import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
import com.ai.st.microservice.ili.dto.RequestIli2pgImportDto;
import com.ai.st.microservice.ili.dto.ResponseImportDto;
import com.ai.st.microservice.ili.services.Ili2pgService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Ili2pg", description = "Import and export file XTF to databases posgresql", tags = { "ili2pg" })
@RestController
@RequestMapping("api/ili/ili2pg/v1")
public class Ili2pgV1Controller {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

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
	public ResponseEntity<ResponseImportDto> schemaImport(@RequestBody RequestIli2pgImportDto ili2pgImportDto) {

		Ili2pgService ili2pg = new Ili2pgService();

		Boolean result = false;
		String message = "";

		try {

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			String logFileSchemaImport = tmpDirectory.toString() + File.separator + "schema_import.log";

			result = ili2pg.generateSchema(logFileSchemaImport, iliDirectory, srsDefault, modelsDefault,
					ili2pgImportDto.getDatabaseHost(), ili2pgImportDto.getDatabasePort(),
					ili2pgImportDto.getDatabaseName(), ili2pgImportDto.getDatabaseSchema(),
					ili2pgImportDto.getDatabaseUsername(), ili2pgImportDto.getDatabasePassword());

			message = (result) ? "Information imported" : "The process could not be performed";

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return new ResponseEntity<>(new ResponseImportDto(result, message), HttpStatus.OK);

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
	public ResponseEntity<IntegrationStatDto> integrationCadestreRegistration(
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

}
