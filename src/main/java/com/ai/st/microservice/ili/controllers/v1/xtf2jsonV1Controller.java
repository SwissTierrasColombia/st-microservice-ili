package com.ai.st.microservice.ili.controllers.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ai.st.microservice.ili.business.Ili2JsonBusiness;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "xtf2json", description = "XTF to JSON", tags = { "xtf" })
@RestController
@RequestMapping("api/ili/xtf2json/v1")
public class xtf2jsonV1Controller {

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Ili2JsonBusiness ili2jsonBusiness;

	@GetMapping("/health")
	public String health(Model model) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		String date_time = dtf.format(now);
		model.addAttribute("date_time", date_time);
		return "health";
	}

	@RequestMapping(value = "shp2json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Convert SHP to GeoJSON")
	@ResponseBody
	public ResponseEntity<InputStreamResource> convertShp2Json(@RequestParam("file[]") MultipartFile[] uploadfiles) {

		ArrayList<String> files;
		try {
			files = ili2jsonBusiness.shp2Json(uploadfiles);
			String returnFile = null;
			String mediaType = "application/json";
			for (String f : files) {
				returnFile = f;
			}
			File file = new File(returnFile);
			InputStream is = new FileInputStream(file);

			return ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.parseMediaType(mediaType))
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
					.body(new InputStreamResource(is));
		} catch (IOException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "gpkg2json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Convert XTF to GeoJSON")
	@ResponseBody
	public ResponseEntity<InputStreamResource> convertGpkg2Json(@RequestParam("file[]") MultipartFile[] uploadfiles) {
		ArrayList<String> files;
		try {
			files = ili2jsonBusiness.gpkg2Json(uploadfiles);
			String returnFile = null;
			String mediaType = "application/json";
			for (String f : files) {
				returnFile = f;
			}
			File file = new File(returnFile);
			InputStream is = new FileInputStream(file);

			return ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.parseMediaType(mediaType))
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
					.body(new InputStreamResource(is));
		} catch (IOException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "kml2json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Convert KML to GeoJSON")
	@ResponseBody
	public ResponseEntity<InputStreamResource> convertKml2Json(@RequestParam("file[]") MultipartFile[] uploadfiles) {
		ArrayList<String> files;
		try {
			files = ili2jsonBusiness.kml2Json(uploadfiles);
			String returnFile = null;
			String mediaType = "application/json";
			for (String f : files) {
				returnFile = f;
			}
			File file = new File(returnFile);
			InputStream is = new FileInputStream(file);

			return ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.parseMediaType(mediaType))
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
					.body(new InputStreamResource(is));
		} catch (IOException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "supply2json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Convert Supply to GeoJSON")
	@ResponseBody
	public ResponseEntity<InputStreamResource> convertSupply2Json(
			@RequestParam("url") String uploadfiles, 
			@RequestParam(name = "version", required = false) String version) {
		
		ArrayList<String> files;
		
		try {
			files = ili2jsonBusiness.supply2Json(uploadfiles, version);
			String returnFile = null;
			String mediaType = "application/json";
			for (String f : files) {
				/* TODO: Unificar files */
				returnFile = f;
			}
			if (returnFile == null) {
				returnFile = "/tmp/resp.json";
				FileWriter myWriter = new FileWriter("/tmp/resp.json");
				myWriter.write("{}");
				myWriter.close();
				
			}
			File file = new File(returnFile);
			InputStream is = new FileInputStream(file);

			return ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.parseMediaType(mediaType))
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
					.body(new InputStreamResource(is));
		} catch (IOException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
