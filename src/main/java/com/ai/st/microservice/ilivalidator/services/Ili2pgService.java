package com.ai.st.microservice.ilivalidator.services;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;

@Service
public class Ili2pgService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public String import2pg(String fileName, String iliDirectory) {

		String iliDir = iliDirectory;

		String uploadDir = FilenameUtils.getFullPath(fileName);

		String logFileName = FilenameUtils.removeExtension(fileName) + "_import.log";

		Config config = new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);

		config.setFunction(Config.FC_IMPORT); // --schemaimport

		config.setModeldir(uploadDir + ";" + "http://models.interlis.ch/" + ";" + iliDir); // -- modeldir
		config.setCreateFk(Config.CREATE_FK_YES); // --createFk

		config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE); // --coalesceCatalogueRef
		config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE); // --coalesceMultiSurface
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2); // --smart2iNHERITANCE
		config.setSetupPgExt(true); // --setupPgExt
		config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE); // --coalesceMultiLine
		config.setCreateUniqueConstraints(true); // --createUnique
		config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE); // --beautifyEnumDispName
		config.setCreateFkIdx(Config.CREATE_FKIDX_YES); // --createFkIdx
		config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID); // --createEnumTabsWithId
		config.setDefaultSrsCode("3116"); // --defaultSrsCode
		config.setCreateNumChecks(true); // --createNumChecks
		config.setValue(Config.CREATE_GEOM_INDEX, Config.TRUE); // --createGeomIdx
		config.setCreateMetaInfo(true); // --createMetaInfo
		Config.setStrokeArcs(config, Config.STROKE_ARCS_ENABLE); // --strokeArcs

		// config.setTidHandling(Config.TID_HANDLING_PROPERTY); // --createTidCol
		// config.setBasketHandling(Config.BASKET_HANDLING_READWRITE); //
		// --createBasketCol
		// config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND); //
		// --expandMultilingual

		config.setXtffile(fileName);
		if (fileName != null && Ili2db.isItfFilename(fileName)) {
			config.setItfTransferfile(true);
		}

		config.setDburl("jdbc:postgresql://" + "localhost" + ":" + "5432" + "/" + "ili2_pg_test");
		config.setDbschema("test6");
		config.setDbusr("postgres");
		config.setDbpwd("123456");

		config.setModels(
				"Cartografia_Referencia_V2_9_6;"
				+ "Avaluos_V2_9_6;"
				+ "Operacion_V2_9_6;"
				+ "LADM_COL_V1_2;"
				+ "Formulario_Catastro_V2_9_6;"
				+ "ISO19107_PLANAS_V1;"
				+ "Datos_Gestor_Catastral_V2_9_6;"
				+ "Datos_SNR_V2_9_6;"
				+ "Datos_Integracion_Insumos_V2_9_6"); // --models

		config.setLogfile(logFileName); // --log

		try {
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config, null);
		} catch (Ili2dbException e) {
			log.error(e.getMessage());
		}

		return logFileName;
	}

}
