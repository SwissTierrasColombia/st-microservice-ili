package ch.so.agi.interlis.services;

import org.springframework.stereotype.Service;

import ch.ehi.basics.settings.Settings;
import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.interlis2.validator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IlivalidatorService {

	private final String logExt = ".log";
	private final String xtfLogExt = "_log.xtf";
	private final String tomlExt = ".toml";

	private final Logger log = LoggerFactory.getLogger(IlivalidatorService.class);

	public Boolean validate(String fileName, String iliDirectory) {

		Boolean result = false;
		String iliDir = iliDirectory;

		String baseFileName = FilenameUtils.getFullPath(fileName) + FilenameUtils.getBaseName(fileName);
		String uploadDir = FilenameUtils.getFullPath(fileName);

		Settings settings = new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, uploadDir + ";" + "http://models.interlis.ch/" + ";" + iliDir);
		settings.setValue(Validator.SETTING_LOGFILE, baseFileName + logExt);
		settings.setValue(Validator.SETTING_XTFLOG, baseFileName + xtfLogExt);

		File toml = new File(baseFileName + tomlExt);
		if (toml.exists() && !toml.isDirectory()) {
			settings.setValue(Validator.SETTING_CONFIGFILE, baseFileName + tomlExt);
		}

		settings.setValue(Validator.SETTING_PLUGINFOLDER, iliDir + File.separatorChar + "plugins");

		try {

			List<String> list = new ArrayList<>();
			list.add("java");
			list.add("-jar");
			list.add("lib/ilivalidator-1.8.1.jar");
			list.add("--modeldir");
			list.add(uploadDir + ";" + "http://models.interlis.ch/" + ";" + iliDir);
			list.add("--log");
			list.add(baseFileName + logExt);
			list.add("--xtflog");
			list.add(baseFileName + xtfLogExt);
			if (toml.exists() && !toml.isDirectory()) {
				list.add("--config");
				list.add(baseFileName + tomlExt);
			}
			list.add("--plugins");
			list.add(iliDir + File.separatorChar + "plugins");
			list.add(fileName);

			String[] parameters = list.toArray(new String[0]);
			Process ps = Runtime.getRuntime().exec(parameters);

			InputStream stderr = ps.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				log.info(line);
			}

			ps.waitFor();

			File file = new File(baseFileName + logExt);
			String res = tail(file);
			if (res.contains("validation done")) {
				result = true;
			} else if (res.contains("validation failed")) {
				result = false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	public String tail(File file) {
		RandomAccessFile fileHandler = null;
		try {
			fileHandler = new RandomAccessFile(file, "r");
			long fileLength = fileHandler.length() - 1;
			StringBuilder sb = new StringBuilder();

			for (long filePointer = fileLength; filePointer != -1; filePointer--) {
				fileHandler.seek(filePointer);
				int readByte = fileHandler.readByte();

				if (readByte == 0xA) {
					if (filePointer == fileLength) {
						continue;
					}
					break;

				} else if (readByte == 0xD) {
					if (filePointer == fileLength - 1) {
						continue;
					}
					break;
				}

				sb.append((char) readByte);
			}

			String lastLine = sb.reverse().toString();
			return lastLine;
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fileHandler != null) {
				try {
					fileHandler.close();
				} catch (IOException e) {
					/* ignore */
				}
			}
		}
	}
}
