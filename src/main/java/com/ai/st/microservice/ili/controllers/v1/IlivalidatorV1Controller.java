package com.ai.st.microservice.ili.controllers.v1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ai.st.microservice.ili.dto.BasicResponseDto;
import com.ai.st.microservice.ili.dto.IlivalidatorBackgroundDto;
import com.ai.st.microservice.ili.dto.ResponseImportDto;
import com.ai.st.microservice.ili.dto.ValidationDto;
import com.ai.st.microservice.ili.exceptions.InputValidationException;
import com.ai.st.microservice.ili.services.IlivalidatorService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Ilivalidator", description = "Validations XTF files", tags = { "ilivalidator" })
@RestController
@RequestMapping("api/ili/ilivalidator/v1")
public class IlivalidatorV1Controller {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

	@Value("${iliProcesses.downloadedFiles}")
	private String downloadedFiles;

	@Value("${iliProcesses.iliDirectory}")
	private String iliDirectory;

	@Value("${iliProcesses.temporalDirectoryPrefix}")
	private String temporalDirectoryPrefix;

	@Value("${iliProcesses.iliDirectoryPlugins}")
	private String iliDirectoryPlugins;

	@Autowired
	private RabbitMQSenderService rabbitSenderService;

	@RequestMapping(value = "validate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = "Validate XTF")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Processed file", response = ValidationDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<List<ValidationDto>> validateXTF(
			@RequestParam(name = "filesXTF[]", required = true) MultipartFile[] uploadfiles,
			@RequestParam(name = "filesModels[]", required = false) MultipartFile[] iliFiles) {

		IlivalidatorService ilivalidator = new IlivalidatorService();

		List<ValidationDto> listValidations = new ArrayList<>();

		try {

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			// Upload model files
			for (MultipartFile iliFile : iliFiles) {
				String iliFileName = iliFile.getOriginalFilename();
				if (!iliFileName.equals("")) {
					String ilifilepath = Paths.get(tmpDirectory.toString(), iliFileName).toString();
					try (BufferedOutputStream ilistream = new BufferedOutputStream(
							new FileOutputStream(new File(ilifilepath)))) {
						ilistream.write(iliFile.getBytes());
					}
				}
			}

			for (MultipartFile uploadfile : uploadfiles) {

				String filename = uploadfile.getOriginalFilename();
				String filepath = Paths.get(tmpDirectory.toString(), filename).toString();

				try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)))) {
					stream.write(uploadfile.getBytes());
				}

				List<String> evaluableFiles = new ArrayList<>();

				Boolean result = false;
				if (FilenameUtils.getExtension(filename).equals("zip")) {

					ZipFile zipFile = new ZipFile(filepath);
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						InputStream stream = zipFile.getInputStream(entry);

						String fileEntryOutName = FilenameUtils.getFullPath(filepath) + entry.getName();
						FileOutputStream outputStream = new FileOutputStream(new File(fileEntryOutName));

						int read = 0;
						byte[] bytes = new byte[1024];

						while ((read = stream.read(bytes)) != -1) {
							outputStream.write(bytes, 0, read);
						}
						stream.close();
						outputStream.close();

						if (FilenameUtils.getExtension(entry.getName()).equals("xtf")) {
							evaluableFiles.add(fileEntryOutName);
						}

					}
					zipFile.close();

				} else {
					evaluableFiles.add(filepath);
				}

				if (evaluableFiles.size() > 0) {
					for (String file : evaluableFiles) {

						String localModelsDirectory = tmpDirectory.toString();
						String logFileValidation = tmpDirectory.toString() + File.separator + "validation.log";
						String logFileValidationXTF = tmpDirectory.toString() + File.separator + "validation.xtf";

						result = ilivalidator.validate(file, iliDirectory, localModelsDirectory, iliDirectoryPlugins,
								logFileValidation, logFileValidationXTF, null);

						String resultId = Paths.get(FilenameUtils.getFullPath(file)).getFileName().getName(0)
								.toString();
						String transfer = FilenameUtils.getName(file);
						listValidations.add(new ValidationDto(resultId, transfer, result, true, true));
					}
				}
			}

			return new ResponseEntity<>(listValidations, HttpStatus.OK);
		} catch (IOException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "validate/background", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Export ")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Integration done", response = ResponseImportDto.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public ResponseEntity<?> validateBackground(@RequestBody IlivalidatorBackgroundDto requestIlivadatorDto) {

		HttpStatus httpStatus = null;
		Object responseDto = null;

		try {

			// validation path file
			String pathFile = requestIlivadatorDto.getPathFile();
			if (pathFile.isEmpty()) {
				throw new InputValidationException("La ruta del archivo a generar es requerida.");
			}

			rabbitSenderService.sendDataToValidator(requestIlivadatorDto);

			httpStatus = HttpStatus.OK;
			responseDto = new BasicResponseDto("¡Validation started!", 5);

		} catch (InputValidationException e) {
			log.error("Error Ili2pgV1Controller@validateBackground#Validation ---> " + e.getMessage());
			httpStatus = HttpStatus.BAD_REQUEST;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		} catch (Exception e) {
			log.error("Error Ili2pgV1Controller@validateBackground#General ---> " + e.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			responseDto = new BasicResponseDto(e.getMessage(), 3);
		}

		return new ResponseEntity<>(responseDto, httpStatus);
	}

}