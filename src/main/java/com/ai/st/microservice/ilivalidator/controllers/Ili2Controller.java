package com.ai.st.microservice.ilivalidator.controllers;

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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ai.st.microservice.ilivalidator.services.Ili2pgService;
import com.ai.st.microservice.ilivalidator.swagger.api.transfers.ValidationModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Ili2", description = "Import and export file XTF to databases", tags = { "ili2" })
@RestController
@RequestMapping("api/ili2")
public class Ili2Controller {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${iliProcesses.temporalDirectoryPrefix}")
	private String temporalDirectoryPrefix;

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

	@Value("${iliProcesses.iliDirectory}")
	private String iliDirectory;

	@RequestMapping(value = "import/pg", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = "Import file XTF to database")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Processed file", response = ValidationModel.class),
			@ApiResponse(code = 500, message = "Error Server", response = String.class) })
	@ResponseBody
	public void importXtf(@RequestParam(name = "fileXTF", required = true) MultipartFile uploadFile) {

		Ili2pgService ili2pg = new Ili2pgService();

		try {

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			String filename = uploadFile.getOriginalFilename();
			String filepath = Paths.get(tmpDirectory.toString(), filename).toString();

			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)))) {
				stream.write(uploadFile.getBytes());
			}

			String log = ili2pg.import2pg(filepath, iliDirectory);
			System.out.println(log);

		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

}
