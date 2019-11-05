package com.ai.st.microservice.ilivalidator.services;

import org.springframework.stereotype.Service;

import ch.ehi.basics.settings.Settings;

import org.interlis2.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IlivalidatorService {

	private final Logger log = LoggerFactory.getLogger(IlivalidatorService.class);

	private static final String MODELS_INTERLIS_CH = "http://models.interlis.ch";

	public Boolean validate(String fileXTF, String iliDirectory, String modelsDirectory, String iliPluginsDirectory,
			String logFileValidation, String logFileValidationXTF, String fileConfigurationToml) {

		Boolean result = false;

		try {

			Settings settings = new Settings();

			String iliDirs = modelsDirectory + ";" + MODELS_INTERLIS_CH + ";" + iliDirectory;

			settings.setValue(Validator.SETTING_ILIDIRS, iliDirs);
			settings.setValue(Validator.SETTING_LOGFILE, logFileValidation);
			settings.setValue(Validator.SETTING_XTFLOG, logFileValidationXTF);
			settings.setValue(Validator.SETTING_PLUGINFOLDER, iliPluginsDirectory);

			if (fileConfigurationToml != null) {
				settings.setValue(Validator.SETTING_CONFIGFILE, fileConfigurationToml);
			}

			result = Validator.runValidation(fileXTF, settings);
		} catch (Exception e) {
			log.error(e.getMessage());
			result = false;
		}

		return result;
	}

}
