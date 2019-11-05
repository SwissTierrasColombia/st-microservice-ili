package com.ai.st.microservice.ilivalidator.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;

@Service
public class Ili2pgService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String MODELS_INTERLIS_CH = "http://models.interlis.ch";

	public Config getDefaultConfig() {

		Config config = new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);

		config.setCreateFk(Config.CREATE_FK_YES); // --createFk
		config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE); // --coalesceCatalogueRef
		config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE); // --coalesceMultiSurface
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2); // --smart2Inheritance
		config.setSetupPgExt(true); // --setupPgExt
		config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE); // --coalesceMultiLine
		config.setCreateUniqueConstraints(true); // --createUnique
		config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE); // --beautifyEnumDispName
		config.setCreateFkIdx(Config.CREATE_FKIDX_YES); // --createFkIdx
		config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID); // --createEnumTabsWithId
		config.setCreateNumChecks(true); // --createNumChecks
		config.setValue(Config.CREATE_GEOM_INDEX, Config.TRUE); // --createGeomIdx
		config.setCreateMetaInfo(true); // --createMetaInfo
		Config.setStrokeArcs(config, Config.STROKE_ARCS_ENABLE); // --strokeArcs

		// config.setTidHandling(Config.TID_HANDLING_PROPERTY); // --createTidCol
		// config.setBasketHandling(Config.BASKET_HANDLING_READWRITE); //
		// --createBasketCol
		// config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND); //
		// --expandMultilingual

		return config;
	}

	public Boolean generateSchema(String logFileSchemaImport, String iliDirectory, String srsCode, String models,
			String databaseHost, String databasePort, String databaseName, String databaseSchema,
			String databaseUsername, String databasePassword) {

		Boolean result = false;

		Config config = getDefaultConfig();

		config.setFunction(Config.FC_SCHEMAIMPORT); // --schemaimport
		config.setModeldir(MODELS_INTERLIS_CH + ";" + iliDirectory); // -- modeldir
		config.setDefaultSrsCode(srsCode); // --defaultSrsCode
		config.setModels(models); // --models
		config.setLogfile(logFileSchemaImport); // --log

		config.setDburl("jdbc:postgresql://" + databaseHost + ":" + databasePort + "/" + databaseName);
		config.setDbschema(databaseSchema);
		config.setDbusr(databaseUsername);
		config.setDbpwd(databasePassword);

		try {
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config, null);
			result = true;
		} catch (Ili2dbException e) {
			log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	public Boolean import2pg(String fileXTF, String logFileSchemaImport, String logFileImport, String iliDirectory,
			String srsCode, String models, String databaseHost, String databasePort, String databaseName,
			String databaseSchema, String databaseUsername, String databasePassword) {

		Boolean result = false;

		Boolean generateSchema = generateSchema(logFileSchemaImport, iliDirectory, srsCode, models, databaseHost,
				databasePort, databaseName, databaseSchema, databaseUsername, databasePassword);

		if (generateSchema) {

			Config config = getDefaultConfig();

			config.setFunction(Config.FC_IMPORT); // --schemaimport
			config.setModeldir(MODELS_INTERLIS_CH + ";" + iliDirectory); // -- modeldir
			config.setDefaultSrsCode(srsCode); // --defaultSrsCode
			config.setModels(models); // --models
			config.setLogfile(logFileImport); // --log
			config.setXtffile(fileXTF);
			if (fileXTF != null && Ili2db.isItfFilename(fileXTF)) {
				config.setItfTransferfile(true);
			}

			config.setDburl("jdbc:postgresql://" + databaseHost + ":" + databasePort + "/" + databaseName);
			config.setDbschema(databaseSchema);
			config.setDbusr(databaseUsername);
			config.setDbpwd(databasePassword);

			try {
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config, null);
				result = true;
			} catch (Ili2dbException e) {
				log.error(e.getMessage());
				result = false;
			}
		}

		return result;
	}

}
