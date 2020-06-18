package com.ai.st.microservice.ili.business;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ai.st.microservice.ili.services.Ili2JsonService;

import ch.interlis.ili2c.metamodel.TransferDescription;

@Component
public class Ili2JsonBusiness {

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

	@Value("${iliProcesses.downloadedFiles}")
	private String downloadedFiles;

	@Value("${iliProcesses.iliDirectory}")
	private String iliDirectory;

	@Value("${iliProcesses.iliDirectoryPlugins}")
	private String iliDirectoryPlugins;

	@Value("${iliProcesses.ogrPath}")
	private String ogrPath;
	
	@Value("${iliProcesses.temporalDirectoryPrefix}")
	private String temporalDirectoryPrefix;
	
	public String ili2Json(MultipartFile[] uploadfiles, MultipartFile[] iliFiles) throws IOException {
		Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), temporalDirectoryPrefix);
		return ili2Json(multipartFileTArray(uploadfiles, tmpDirectory), multipartFileTArray(iliFiles, tmpDirectory), tmpDirectory);
	}

	public String ili2Json(MultipartFile[] uploadfiles, MultipartFile[] iliFiles, Path tmpDirectory) throws IOException {
		return ili2Json(multipartFileTArray(uploadfiles, tmpDirectory), multipartFileTArray(iliFiles, tmpDirectory), tmpDirectory);
	}

	public String ili2Json(ArrayList<File> uploadfiles, MultipartFile[] iliFiles, Path tmpDirectory) throws IOException {
		return ili2Json(uploadfiles, multipartFileTArray(iliFiles, tmpDirectory), tmpDirectory);
	}

	public String ili2Json(ArrayList<File> uploadfiles, ArrayList<File> iliFiles, Path tmpDirectory) throws IOException {

		Ili2JsonService ili2json = new Ili2JsonService();
		ili2json.setEnv(uploadedFiles, downloadedFiles, iliDirectory, iliDirectoryPlugins, ogrPath);

		// Create temporal directory

		String out = "";

		Map classesModels = new HashMap();

		// Upload model files
		for (File iliFile : iliFiles) {
			String iliFileName = iliFile.getName();
			if (!iliFileName.equals("")) {
				String ilifilepath = Paths.get(tmpDirectory.toString(), iliFileName).toString();

				/*try ( // Save the file locally
						BufferedOutputStream ilistream = new BufferedOutputStream(
								new FileOutputStream(new File(ilifilepath)))) {
					ilistream.write(this.readFileToByteArray(iliFile));
				}*/

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

			/*try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(pathXTF)))) {
				stream.write(this.readFileToByteArray(uploadfile));
			}*/

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
			out += ili2json.writeOutIli2Json(tmpDirectory.getFileName().getName(0).toString(), fileXTF, items);

		}

		if (out.lastIndexOf(",") != -1) {
			out = out.substring(0, out.lastIndexOf(","));
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

	/**
	 * This method uses java.io.FileInputStream to read
	 * https://www.netjstech.com/2015/11/how-to-convert-file-to-byte-array-java.html
	 * file content into a byte array
	 * 
	 * @param file
	 * @return
	 */
	private byte[] readFileToByteArray(File file) {
		FileInputStream fis = null;
		// Creating a byte array using the length of the file
		// file.length returns long which is cast to int
		byte[] bArray = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			fis.read(bArray);
			fis.close();

		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
		return bArray;
	}
	

	public ArrayList<String> shp2Json(MultipartFile[] uploadfiles) throws IOException{
		Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), temporalDirectoryPrefix);
		return shp2Json(multipartFileTArray(uploadfiles, tmpDirectory), tmpDirectory);
	}

	public ArrayList<String> shp2Json(ArrayList<File> uploadfiles, Path tmpDirectory) throws IOException{
		ArrayList<String> resp =  new ArrayList<>();
		Ili2JsonService ili2json = new Ili2JsonService();
		ili2json.setEnv(uploadedFiles, downloadedFiles, iliDirectory, iliDirectoryPlugins, ogrPath);
		for (File uploadfile : uploadfiles) {
			resp.add(ili2json.shp2json(uploadfile));
		}
		return resp;
	}

	public ArrayList<String> gpkg2Json(MultipartFile[] uploadfiles) throws IOException {
		Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), temporalDirectoryPrefix);
		return gpkg2Json(multipartFileTArray(uploadfiles, tmpDirectory), tmpDirectory);
	}
	
	public ArrayList<String> gpkg2Json(ArrayList<File> uploadfiles, Path tmpDirectory) throws IOException {
		ArrayList<String> resp =  new ArrayList<>();
		Ili2JsonService ili2json = new Ili2JsonService();
		ili2json.setEnv(uploadedFiles, downloadedFiles, iliDirectory, iliDirectoryPlugins, ogrPath);
		for (File uploadfile : uploadfiles) {
			resp.add(ili2json.gpkg2json(uploadfile));
		}
		return resp;
	}

}
