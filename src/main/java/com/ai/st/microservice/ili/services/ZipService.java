package com.ai.st.microservice.ili.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ZipService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public String zipDirectory(String dir, String fileName) throws IOException {
		String zipFileName = FilenameUtils.getFullPath(fileName) + FilenameUtils.getBaseName(fileName) + ".zip";

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		Path p = Paths.get(zipFileName);
		URI uri = null;
		try {
			uri = new URI("jar", p.toUri().toString(), null);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// URI uri = URI.create("jar:file://" + zipFileName);
		log.debug("uri: " + uri.toString());
		try (FileSystem zipFileSystem = FileSystems.newFileSystem(uri, env)) {
			log.debug(zipFileName + " created.");

			// It also possible to use a FileVisitor here.
			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(new File(dir).toURI()))) {
				for (Path path : directoryStream) {
					if (Files.isRegularFile(path)) {

						// the file to be added to the zipfile
						String zFile = path.toFile().getAbsolutePath();

						// do not add original file to zipfile
						if (zFile.equalsIgnoreCase(fileName)) {
							continue;
						}

						// do not add zipfile itself to zipfile
						String zFileExtension = FilenameUtils.getExtension(zFile);
						log.debug(zFileExtension);
						if (zFileExtension.equalsIgnoreCase("zip")) {
							continue;
						}

						Path pathInZipfile = zipFileSystem.getPath(path.toFile().getName());
						Files.copy(path, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
			log.debug("All files added to zipfile.");
		}
		return zipFileName;
	}

	public List<String> unzip(String fileZip, File destDir) throws IOException {

		List<String> paths = new ArrayList<>();

		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			File newFile = newFile(destDir, zipEntry);
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();

			paths.add(newFile.getName());

		}
		zis.closeEntry();
		zis.close();

		return paths;
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {

		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

}
