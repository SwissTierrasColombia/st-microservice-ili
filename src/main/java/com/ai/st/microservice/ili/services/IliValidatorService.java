package com.ai.st.microservice.ili.services;

import com.ai.st.microservice.ili.services.tracing.SCMTracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ehi.basics.settings.Settings;

import org.interlis2.validator.Validator;

@Service
public class IliValidatorService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Boolean validate(String fileXTF, String iliDirectory, String modelsDirectory, String iliPluginsDirectory,
            String logFileValidation, String logFileValidationXTF, String fileConfigurationToml) {

        boolean result;

        try {

            Settings settings = new Settings();

            String iliDirs = modelsDirectory + ";" + iliDirectory;

            settings.setValue(Validator.SETTING_ILIDIRS, iliDirs);
            settings.setValue(Validator.SETTING_LOGFILE, logFileValidation);

            if (logFileValidationXTF != null) {
                settings.setValue(Validator.SETTING_XTFLOG, logFileValidationXTF);
            }

            if (iliPluginsDirectory != null) {
                settings.setValue(Validator.SETTING_PLUGINFOLDER, iliPluginsDirectory);
            }

            if (fileConfigurationToml != null) {
                settings.setValue(Validator.SETTING_CONFIGFILE, fileConfigurationToml);
            }

            result = Validator.runValidation(fileXTF, settings);
        } catch (Exception e) {
            String messageError = String.format("Error validando el archivo XTF : %s", e.getMessage());
            SCMTracing.sendError(messageError);
            log.error(messageError);
            result = false;
        }

        return result;
    }

}
