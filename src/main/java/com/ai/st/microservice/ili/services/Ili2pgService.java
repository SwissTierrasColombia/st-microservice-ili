package com.ai.st.microservice.ili.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.drivers.PostgresDriver;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;

import ch.ehi.ili2db.base.Ili2db;
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

		try {

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

			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config, null);
			result = true;
		} catch (Exception e) {
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

			try {

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

				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config, null);
				result = true;
			} catch (Exception e) {
				log.error(e.getMessage());
				result = false;
			}
		}

		return result;
	}

	public IntegrationStatDto integration(String cadastreFileXTF, String cadastreLogFileSchemaImport,
			String cadastreLogFileImport, String registrationFileXTF, String registrationLogFileSchemaImport,
			String registrationLogFileImport, String iliDirectory, String srsCode, String models, String databaseHost,
			String databasePort, String databaseName, String databaseSchema, String databaseUsername,
			String databasePassword) {

		IntegrationStatDto integrationStat = new IntegrationStatDto();

		// load cadastral information
		Boolean loadCadastral = this.import2pg(cadastreFileXTF, cadastreLogFileSchemaImport, cadastreLogFileImport,
				iliDirectory, srsCode, models, databaseHost, databasePort, databaseName, databaseSchema,
				databaseUsername, databasePassword);

		// load registration information
		Boolean loadRegistration = this.import2pg(registrationFileXTF, registrationLogFileSchemaImport,
				registrationLogFileImport, iliDirectory, srsCode, models, databaseHost, databasePort, databaseName,
				databaseSchema, databaseUsername, databasePassword);

		if (loadCadastral && loadRegistration) {

			PostgresDriver connection = new PostgresDriver();

			String urlConnection = "jdbc:postgresql://" + databaseHost + ":" + databasePort + "/" + databaseName;
			connection.connect(urlConnection, databaseUsername, databasePassword, "org.postgresql.Driver");

			String sqlObjects = "select  \r\n" + "	  snr_p.t_id as snr_predio_juridico\r\n"
					+ "	, gc.t_id as gc_predio_catastro\r\n" + "from " + databaseSchema
					+ ".snr_predio_juridico as snr_p\r\n" + "inner join " + databaseSchema
					+ ".gc_predio_catastro as gc\r\n" + "	on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial\r\n"
					+ "	and ltrim(snr_p.matricula_inmobiliaria,'0')=trim(gc.matricula_inmobiliaria_catastro)\r\n"
					+ "	and snr_p.codigo_orip = gc.circulo_registral;";
			ResultSet resultsetObjects = connection.getResultSetFromSql(sqlObjects);

			try {

				while (resultsetObjects.next()) {

					String snr = resultsetObjects.getString("snr_predio_juridico");
					String gc = resultsetObjects.getString("gc_predio_catastro");

					String sqlInsert = "INSERT INTO " + databaseSchema
							+ ".ini_predio_insumos (gc_predio_catastro, snr_predio_juridico) VALUES (" + gc + ", " + snr
							+ ");";
					connection.insert(sqlInsert);

				}

				String sqlCountSNR = "SELECT count(*) FROM " + databaseSchema + ".snr_predio_juridico;";
				long countSNR = connection.count(sqlCountSNR);

				String sqlCountGC = "SELECT count(*) FROM " + databaseSchema + ".gc_predio_catastro;";
				long countGC = connection.count(sqlCountGC);

				String sqlCountMatch = "SELECT count(*) FROM " + databaseSchema + ".ini_predio_insumos;";
				long countMatch = connection.count(sqlCountMatch);

				double percentage = 0.0;

				if (countSNR >= countGC) {
					percentage = (double) (countMatch * 100) / countSNR;
				} else {
					percentage = (double) (countMatch * 100) / countGC;
				}

				integrationStat.setStatus(true);
				integrationStat.setCountGC(countGC);
				integrationStat.setCountSNR(countSNR);
				integrationStat.setCountMatch(countMatch);
				integrationStat.setPercentage(percentage);

			} catch (SQLException e) {
				integrationStat.setStatus(false);
			}

		}

		return integrationStat;
	}

	public Boolean exportToXtf(String filePath, String logFileExport, String iliDirectory, String srsCode,
			String models, String databaseHost, String databasePort, String databaseName, String databaseSchema,
			String databaseUsername, String databasePassword) {

		Boolean result = false;

		try {

			Config config = getDefaultConfig();

			config.setFunction(Config.FC_EXPORT); // --schemaimport
			config.setModeldir(MODELS_INTERLIS_CH + ";" + iliDirectory); // -- modeldir
			config.setDefaultSrsCode(srsCode); // --defaultSrsCode
			config.setModels(models); // --models
			config.setLogfile(logFileExport); // --log

			config.setXtffile(filePath);

			config.setDburl("jdbc:postgresql://" + databaseHost + ":" + databasePort + "/" + databaseName);
			config.setDbschema(databaseSchema);
			config.setDbusr(databaseUsername);
			config.setDbpwd(databasePassword);

			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config, null);
			result = true;
		} catch (Exception e) {
			log.error("ERROR EXPORT: " + e.getMessage());
			result = false;
		}

		return result;
	}

}
