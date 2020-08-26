package com.ai.st.microservice.ili.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.exceptions.BusinessException;
import com.ai.st.microservice.ili.services.Ili2JsonService;

import ch.interlis.ili2c.metamodel.TransferDescription;
import net.bytebuddy.utility.RandomString;

@Component
public class Ili2JsonBusiness {

	@Value("${iliProcesses.ogrPath}")
	private String ogrPath;
	
	@Value("${st.temporalDirectory}")
	private String stTemporalDirectory;
	
	@Autowired
	private VersionBusiness versionBusiness;
	
	public String ili2Json(MultipartFile[] uploadfiles, MultipartFile[] iliFiles, String realIliDirectory) throws IOException {
		
		String nameDirectory = "ili_process_" + RandomStringUtils.random(7, false, true);
		Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);
		
		return ili2Json(multipartFileTArray(uploadfiles, tmpDirectory), multipartFileTArray(iliFiles, tmpDirectory),
				tmpDirectory, realIliDirectory);
	}

	public String ili2Json(MultipartFile[] uploadfiles, MultipartFile[] iliFiles, Path tmpDirectory, String realIliDirectory)
			throws IOException {
		return ili2Json(multipartFileTArray(uploadfiles, tmpDirectory), multipartFileTArray(iliFiles, tmpDirectory),
				tmpDirectory, realIliDirectory);
	}

	public String ili2Json(ArrayList<File> uploadfiles, MultipartFile[] iliFiles, Path tmpDirectory, String realIliDirectory)
			throws IOException {
		return ili2Json(uploadfiles, multipartFileTArray(iliFiles, tmpDirectory), tmpDirectory, realIliDirectory);
	}

	public String ili2Json(ArrayList<File> uploadfiles, ArrayList<File> iliFiles, Path tmpDirectory, String realIliDirectory)
			throws IOException {

		Ili2JsonService ili2json = new Ili2JsonService();
		ili2json.setEnv(stTemporalDirectory, stTemporalDirectory, realIliDirectory, "", ogrPath);

		// Create temporal directory

		String out = "";

		Map classesModels = new HashMap();

		// Upload model files
		for (File iliFile : iliFiles) {
			String iliFileName = iliFile.getName();
			if (!iliFileName.equals("")) {
				String ilifilepath = Paths.get(tmpDirectory.toString(), iliFileName).toString();

				/*
				 * try ( // Save the file locally BufferedOutputStream ilistream = new
				 * BufferedOutputStream( new FileOutputStream(new File(ilifilepath)))) {
				 * ilistream.write(this.readFileToByteArray(iliFile)); }
				 */

				TransferDescription td = ili2json.getTansfDesc(ilifilepath); // Convert ili 2 imd

				if (td != null) {

					Map classes = ili2json.getClassesTransDesc(td,
							iliFileName.substring(0, iliFileName.lastIndexOf('.')));

					if (!classes.isEmpty()) {
						classesModels.putAll(classes);
					}

					ili2json.td2imd(ilifilepath, td);
				}
			}
		}

		// upload xtf
		for (File uploadfile : uploadfiles) {

			// Get the filename and build the local file path
			String fileXTF = uploadfile.getName();
			String pathXTF = Paths.get(tmpDirectory.toString(), fileXTF).toString();
			String workingDir = tmpDirectory.toString();

			/*
			 * try (BufferedOutputStream stream = new BufferedOutputStream(new
			 * FileOutputStream(new File(pathXTF)))) {
			 * stream.write(this.readFileToByteArray(uploadfile)); }
			 */

			ArrayList<String> iliModels = ili2json.getIliModels(pathXTF);

			iliModels.forEach((iliModel) -> {

				TransferDescription td = ili2json.getTansfDesc(iliModel);

				if (td != null) {

					ili2json.td2imd(iliModel, td, workingDir);
					String nameIliModel = new File(iliModel).getName();
					Map classes = ili2json.getClassesTransDesc(td,
							nameIliModel.substring(0, nameIliModel.lastIndexOf('.')));
					if (!classes.isEmpty()) {
						classesModels.putAll(classes);
					}
				}
			});

			List outFiles = ili2json.translate(pathXTF, classesModels);

			// check generate files
			HashMap items = ili2json.checkGenerateFile(outFiles);

			// get output to write
			out += ili2json.writeOutIli2Json2(tmpDirectory.getFileName().getName(0).toString(), fileXTF, items);

		}

		return out;
	}

	public ArrayList<File> multipartFileTArray(MultipartFile[] files, Path tmpDirectory) throws IOException {
		ArrayList<File> list = new ArrayList<>();
		for (MultipartFile f : files) {
			File convFile = new File(tmpDirectory.toString(), f.getOriginalFilename());
			FileUtils.writeByteArrayToFile(convFile, f.getBytes());
			list.add(convFile);
		}
		return list;
	}

	public ArrayList<String> shp2Json(MultipartFile[] uploadfiles) throws IOException {
		String nameDirectory = "ili_process_" + RandomStringUtils.random(7, false, true);
		Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);
		return shp2Json(multipartFileTArray(uploadfiles, tmpDirectory), tmpDirectory);
	}

	public ArrayList<String> shp2Json(ArrayList<File> uploadfiles, Path tmpDirectory) throws IOException {
		ArrayList<String> resp = new ArrayList<>();
		Ili2JsonService ili2json = new Ili2JsonService();
		ili2json.setEnv(stTemporalDirectory, stTemporalDirectory, "", "", ogrPath);
		for (File uploadfile : uploadfiles) {
			resp.add(ili2json.shp2json(uploadfile));
		}
		return resp;
	}

	public ArrayList<String> gpkg2Json(MultipartFile[] uploadfiles) throws IOException {
		String nameDirectory = "ili_process_" + RandomStringUtils.random(7, false, true);
		Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);
		return gpkg2Json(multipartFileTArray(uploadfiles, tmpDirectory), tmpDirectory);
	}

	public ArrayList<String> gpkg2Json(ArrayList<File> uploadfiles, Path tmpDirectory) throws IOException {
		ArrayList<String> resp = new ArrayList<>();
		Ili2JsonService ili2json = new Ili2JsonService();
		ili2json.setEnv(stTemporalDirectory, stTemporalDirectory, "", "", ogrPath);
		for (File uploadfile : uploadfiles) {
			resp.add(ili2json.gpkg2json(uploadfile));
		}
		return resp;
	}

	public ArrayList<String> kml2Json(MultipartFile[] uploadfiles) throws IOException {
		String nameDirectory = "ili_process_" + RandomStringUtils.random(7, false, true);
		Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);
		return kml2Json(multipartFileTArray(uploadfiles, tmpDirectory), tmpDirectory);
	}

	public ArrayList<String> kml2Json(ArrayList<File> uploadfiles, Path tmpDirectory) throws IOException {
		ArrayList<String> resp = new ArrayList<>();
		Ili2JsonService ili2json = new Ili2JsonService();
		ili2json.setEnv(stTemporalDirectory, stTemporalDirectory, "", "", ogrPath);
		for (File uploadfile : uploadfiles) {
			resp.add(ili2json.kml2json(uploadfile));
		}
		return resp;
	}

	public ArrayList<String> supply2Json(String uploadfiles, String version) throws IOException {
		ArrayList<String> exts = new ArrayList<>();
		exts.add("shp");
		exts.add("kml");
		exts.add("gpkg");
		exts.add("xtf");
		String filename = this.zipContainsFile(uploadfiles, exts);
		if (filename.length() > 0) {
			if (this.unzipping(uploadfiles)) {
				String path = FilenameUtils.getFullPath(uploadfiles);
				File convFile = new File(path.toString(), filename);
				ArrayList<File> uf = new ArrayList<File>();
				uf.add(convFile);
				switch (FilenameUtils.getExtension(filename)) {
				case "shp":
					return this.shp2Json(uf, Paths.get(path));
				case "kml":
					return this.kml2Json(uf, Paths.get(path));
				case "gpkg":
					return this.gpkg2Json(uf, Paths.get(path));
				case "xtf":
					if (version != null) {
						VersionDataDto versionData;
						try {
							versionData = versionBusiness.getDataVersion(version,
									ConceptBusiness.CONCEPT_OPERATION);
							if (versionData instanceof VersionDataDto) {
								ArrayList<String> rsp = new ArrayList<String>();
								String jsonstr = this.ili2Json(uf, new ArrayList<File>(), Paths.get(path), versionData.getUrl());
								String returnFile = "/tmp/resp"+RandomStringUtils.random(10,true, false)+".json";
								FileWriter myWriter = new FileWriter(returnFile);
								myWriter.write(jsonstr);
								myWriter.close();
								rsp.add(returnFile);
								return rsp;
							}
						} catch (BusinessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return new ArrayList<String>();
	}

	private boolean unzipping(String filePathZip) {

		try {

			ZipFile zipFile = new ZipFile(filePathZip);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				InputStream stream = zipFile.getInputStream(entry);

				String fileEntryOutName = FilenameUtils.getFullPath(filePathZip) + entry.getName();
				FileOutputStream outputStream = new FileOutputStream(new File(fileEntryOutName));

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = stream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				stream.close();
				outputStream.close();

			}

			zipFile.close();

			return true;
		} catch (IOException e) {

		}

		return false;
	}

	private String zipContainsFile(String filePathZip, List<String> extensionsToSearch) {

		String fileFound = "";

		try {

			ZipFile zipFile = new ZipFile(filePathZip);

			for (String extension : extensionsToSearch) {

				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (FilenameUtils.getExtension(entry.getName()).equalsIgnoreCase(extension)) {
						fileFound = entry.getName();
						break;
					}
				}

				if (fileFound.length() > 0) {
					break;
				}

			}

			zipFile.close();
		} catch (IOException e) {
			fileFound = "";
		}

		return fileFound;
	}

}
