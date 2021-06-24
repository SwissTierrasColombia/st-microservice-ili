package com.ai.st.microservice.ili.services;

import org.springframework.stereotype.Service;

import ch.ehi.basics.settings.Settings;

import org.interlis2.validator.Validator;

@Service
public class IlivalidatorService {

    public Boolean validate(String fileXTF, String iliDirectory, String modelsDirectory, String iliPluginsDirectory,
                            String logFileValidation, String logFileValidationXTF, String fileConfigurationToml) {

        boolean result;

        try {

            Settings settings = new Settings();

            String iliDirs = modelsDirectory + ";" + iliDirectory;

            settings.setValue(Validator.SETTING_ILIDIRS, iliDirs);
            settings.setValue(Validator.SETTING_LOGFILE, logFileValidation);
            settings.setValue(Validator.SETTING_XTFLOG, logFileValidationXTF);
            if (iliPluginsDirectory != null) {
                settings.setValue(Validator.SETTING_PLUGINFOLDER, iliPluginsDirectory);
            }

            if (fileConfigurationToml != null) {
                settings.setValue(Validator.SETTING_CONFIGFILE, fileConfigurationToml);
            }

            result = Validator.runValidation(fileXTF, settings);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }


}
