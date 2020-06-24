package com.ai.st.microservice.ili;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.business.ConceptBusiness;
import com.ai.st.microservice.ili.business.QueryTypeBusiness;
import com.ai.st.microservice.ili.entities.ConceptEntity;
import com.ai.st.microservice.ili.entities.ModelEntity;
import com.ai.st.microservice.ili.entities.QueryEntity;
import com.ai.st.microservice.ili.entities.QueryTypeEntity;
import com.ai.st.microservice.ili.entities.VersionConceptEntity;
import com.ai.st.microservice.ili.entities.VersionEntity;
import com.ai.st.microservice.ili.services.IConceptService;
import com.ai.st.microservice.ili.services.IQueryTypeService;
import com.ai.st.microservice.ili.services.IVersionConceptService;
import com.ai.st.microservice.ili.services.IVersionService;

@Component
public class StMicroserviceIliApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(StMicroserviceIliApplicationStartup.class);

	@Autowired
	private IVersionService versionService;

	@Autowired
	private IConceptService conceptService;

	@Autowired
	private IVersionConceptService versionConceptService;

	@Autowired
	private IQueryTypeService queryTypeService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("ST - Loading Domains ... ");
		this.initConcepts();
		this.initQueriesTypes();
		this.initVersions();
	}

	public void initConcepts() {

		Long countConcepts = conceptService.getCount();
		if (countConcepts == 0) {

			try {

				ConceptEntity conceptOperation = new ConceptEntity();
				conceptOperation.setId(ConceptBusiness.CONCEPT_OPERATION);
				conceptOperation.setName("OPERACIÓN");
				conceptService.createConcept(conceptOperation);

				ConceptEntity conceptIntegration = new ConceptEntity();
				conceptIntegration.setId(ConceptBusiness.CONCEPT_INTEGRATION);
				conceptIntegration.setName("INTEGRACIÓN");
				conceptService.createConcept(conceptIntegration);

				log.info("The domains 'concepts' have been loaded!");
			} catch (Exception e) {
				log.error("Failed to load 'concepts' domains");
			}

		}

	}

	public void initQueriesTypes() {

		Long countTypes = queryTypeService.getCount();
		if (countTypes == 0) {

			try {

				QueryTypeEntity typeMatchIntegration = new QueryTypeEntity();
				typeMatchIntegration.setId(QueryTypeBusiness.QUERY_TYPE_MATCH_INTEGRATION);
				typeMatchIntegration.setName("INTEGRACIÓN CATASTRO REGISTRO");
				queryTypeService.createQueryType(typeMatchIntegration);

				QueryTypeEntity typeInsert = new QueryTypeEntity();
				typeInsert.setId(QueryTypeBusiness.QUERY_TYPE_INSERT_INTEGRATION_);
				typeInsert.setName("INSERT INTEGRACIÓN CATASTRO REGISTRO");
				queryTypeService.createQueryType(typeInsert);

				QueryTypeEntity typeCountSnr = new QueryTypeEntity();
				typeCountSnr.setId(QueryTypeBusiness.QUERY_TYPE_COUNT_SNR_INTEGRATION);
				typeCountSnr.setName("COUNT SNR");
				queryTypeService.createQueryType(typeCountSnr);

				QueryTypeEntity typeCountCadastre = new QueryTypeEntity();
				typeCountCadastre.setId(QueryTypeBusiness.QUERY_TYPE_COUNT_CADASTRE_INTEGRATION);
				typeCountCadastre.setName("COUNT CADASTRE");
				queryTypeService.createQueryType(typeCountCadastre);

				QueryTypeEntity typeCountMatch = new QueryTypeEntity();
				typeCountMatch.setId(QueryTypeBusiness.QUERY_TYPE_COUNT_MATCH_INTEGRATION);
				typeCountMatch.setName("COUNT MATCH");
				queryTypeService.createQueryType(typeCountMatch);

				log.info("The domains 'queries types' have been loaded!");
			} catch (Exception e) {
				log.error("Failed to load 'queries types' domains");
			}

		}

	}

	public void initVersions() {

		Long countVersions = versionService.getCount();
		if (countVersions == 0) {

			try {

				QueryTypeEntity queryIntegration = queryTypeService
						.getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_MATCH_INTEGRATION);

				QueryTypeEntity queryInsertIntegration = queryTypeService
						.getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_INSERT_INTEGRATION_);

				QueryTypeEntity queryCountSnr = queryTypeService
						.getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_COUNT_SNR_INTEGRATION);

				QueryTypeEntity queryCountCadastre = queryTypeService
						.getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_COUNT_CADASTRE_INTEGRATION);

				QueryTypeEntity queryCountMatch = queryTypeService
						.getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_COUNT_MATCH_INTEGRATION);

				ConceptEntity conceptOperator = conceptService.getConceptById(ConceptBusiness.CONCEPT_OPERATION);
				ConceptEntity conceptIntegration = conceptService.getConceptById(ConceptBusiness.CONCEPT_INTEGRATION);

				VersionEntity version294 = new VersionEntity();
				version294.setName("2.9.4");
				version294.setCreatedAt(new Date());
				versionService.createVersion(version294);

				VersionConceptEntity versionConceptOperation294 = new VersionConceptEntity();
				versionConceptOperation294.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.4");
				versionConceptOperation294.setVersion(version294);
				versionConceptOperation294.setConcept(conceptOperator);

				List<ModelEntity> models294Operation = new ArrayList<ModelEntity>();
				models294Operation.add(new ModelEntity("Cartografia_Referencia_V2_9_4", versionConceptOperation294));
				models294Operation.add(new ModelEntity("Avaluos_V2_9_4", versionConceptOperation294));
				models294Operation.add(new ModelEntity("ISO19107_V1_MAGNABOG", versionConceptOperation294));
				models294Operation.add(new ModelEntity("Operacion_V2_9_4", versionConceptOperation294));
				models294Operation.add(new ModelEntity("LADM_COL_V1_2", versionConceptOperation294));
				models294Operation.add(new ModelEntity("Formulario_Catastro_V2_9_4", versionConceptOperation294));
				models294Operation.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_4", versionConceptOperation294));
				models294Operation.add(new ModelEntity("Datos_SNR_V2_9_4", versionConceptOperation294));
				models294Operation.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_4", versionConceptOperation294));

				versionConceptOperation294.setModels(models294Operation);

				versionConceptService.createVersionConcept(versionConceptOperation294);

				VersionConceptEntity versionConceptIntegration294 = new VersionConceptEntity();
				versionConceptIntegration294.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.4");
				versionConceptIntegration294.setVersion(version294);
				versionConceptIntegration294.setConcept(conceptIntegration);

				List<ModelEntity> models294Integration = new ArrayList<ModelEntity>();
				models294Integration
						.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_4", versionConceptIntegration294));
				models294Integration.add(new ModelEntity("Datos_SNR_V2_9_4", versionConceptIntegration294));
				models294Integration
						.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_4", versionConceptIntegration294));

				versionConceptIntegration294.setModels(models294Integration);

				List<QueryEntity> querys294 = new ArrayList<>();
				querys294.add(new QueryEntity(versionConceptIntegration294, queryIntegration,
						"select snr_p.t_id as snr_predio_juridico, gc.t_id as gc_predio_catastro from {dbschema}.snr_predio_juridico as snr_p inner"
								+ " join {dbschema}.gc_predio_catastro as gc on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial and "
								+ "ltrim(snr_p.matricula_inmobiliaria,'0')=trim(gc.matricula_inmobiliaria_catastro) and snr_p.codigo_orip = gc.circulo_registral"));
				querys294.add(new QueryEntity(versionConceptIntegration294, queryInsertIntegration,
						"insert into {dbschema}.ini_predio_insumos (gc_predio_catastro, snr_predio_juridico) values ( {cadastre}, {snr})"));
				querys294.add(new QueryEntity(versionConceptIntegration294, queryCountSnr,
						"select count(*) from {dbschema}.snr_predio_juridico"));
				querys294.add(new QueryEntity(versionConceptIntegration294, queryCountCadastre,
						"select count(*) from {dbschema}.gc_predio_catastro"));
				querys294.add(new QueryEntity(versionConceptIntegration294, queryCountMatch,
						"select count(*) from {dbschema}.ini_predio_insumos"));
				versionConceptIntegration294.setQuerys(querys294);

				versionConceptService.createVersionConcept(versionConceptIntegration294);

				VersionEntity version296 = new VersionEntity();
				version296.setName("2.9.6");
				version296.setCreatedAt(new Date());
				versionService.createVersion(version296);

				VersionConceptEntity versionConceptOperation296 = new VersionConceptEntity();
				versionConceptOperation296.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.6");
				versionConceptOperation296.setVersion(version296);
				versionConceptOperation296.setConcept(conceptOperator);

				List<ModelEntity> models296Operation = new ArrayList<ModelEntity>();
				models296Operation.add(new ModelEntity("ANT_V2_9_6", versionConceptOperation296));
				models296Operation.add(new ModelEntity("Cartografia_Referencia_V2_9_6", versionConceptOperation296));
				models296Operation.add(new ModelEntity("Avaluos_V2_9_6", versionConceptOperation296));
				models296Operation.add(new ModelEntity("Operacion_V2_9_6", versionConceptOperation296));
				models296Operation.add(new ModelEntity("LADM_COL_V1_3", versionConceptOperation296));
				models296Operation.add(new ModelEntity("Formulario_Catastro_V2_9_6", versionConceptOperation296));
				models296Operation.add(new ModelEntity("ISO19107_PLANAS_V1", versionConceptOperation296));
				models296Operation.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_6", versionConceptOperation296));
				models296Operation.add(new ModelEntity("Datos_SNR_V2_9_6", versionConceptOperation296));
				models296Operation.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_6", versionConceptOperation296));

				versionConceptOperation296.setModels(models296Operation);

				versionConceptService.createVersionConcept(versionConceptOperation296);

				VersionConceptEntity versionConceptIntegration296 = new VersionConceptEntity();
				versionConceptIntegration296.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.6");
				versionConceptIntegration296.setVersion(version296);
				versionConceptIntegration296.setConcept(conceptIntegration);

				List<ModelEntity> models296Integration = new ArrayList<ModelEntity>();
				models296Integration
						.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_6", versionConceptIntegration296));
				models296Integration.add(new ModelEntity("Datos_SNR_V2_9_6", versionConceptIntegration296));
				models296Integration
						.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_6", versionConceptIntegration296));

				versionConceptIntegration296.setModels(models296Integration);

				List<QueryEntity> querys296 = new ArrayList<>();
				querys296.add(new QueryEntity(versionConceptIntegration296, queryIntegration,
						"select snr_p.t_id as snr_predio_juridico, gc.t_id as gc_predio_catastro from {dbschema}.snr_predio_registro as snr_p inner "
								+ "join {dbschema}.gc_predio_catastro as gc on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial "
								+ "and ltrim(snr_p.matricula_inmobiliaria,'0')=trim(gc.matricula_inmobiliaria_catastro) and snr_p.codigo_orip = gc.circulo_registral"));
				querys296.add(new QueryEntity(versionConceptIntegration296, queryInsertIntegration,
						"insert into {dbschema}.ini_predio_insumos (gc_predio_catastro, snr_predio_juridico) values ( {cadastre}, {snr})"));
				querys296.add(new QueryEntity(versionConceptIntegration296, queryCountSnr,
						"select count(*) from {dbschema}.snr_predio_registro"));
				querys296.add(new QueryEntity(versionConceptIntegration296, queryCountCadastre,
						"select count(*) from {dbschema}.gc_predio_catastro"));
				querys296.add(new QueryEntity(versionConceptIntegration296, queryCountMatch,
						"select count(*) from {dbschema}.ini_predio_insumos"));
				versionConceptIntegration296.setQuerys(querys296);

				versionConceptService.createVersionConcept(versionConceptIntegration296);

				// version 3.0
				VersionEntity version30 = new VersionEntity();
				version30.setName("3.0");
				version30.setCreatedAt(new Date());
				versionService.createVersion(version30);

				VersionConceptEntity versionConceptOperation30 = new VersionConceptEntity();
				versionConceptOperation30.setUrl("/opt/storage-microservice-ili/ladm-col/models/3.0");
				versionConceptOperation30.setVersion(version30);
				versionConceptOperation30.setConcept(conceptOperator);

				List<ModelEntity> models30Operation = new ArrayList<ModelEntity>();
				models30Operation
						.add(new ModelEntity("Submodelo_Cartografia_Catastral_V1_0", versionConceptOperation30));
				models30Operation.add(new ModelEntity("Sumodelo_Avaluos_V1_0", versionConceptOperation30));
				models30Operation.add(new ModelEntity("LADM_COL_V3_0", versionConceptOperation30));
				models30Operation
						.add(new ModelEntity("Modelo_Aplicacion_LADMCOL_Lev_Cat_V1_0", versionConceptOperation30));
				models30Operation.add(new ModelEntity("ISO19107_PLANAS_V3_0", versionConceptOperation30));
				models30Operation
						.add(new ModelEntity("Submodelo_Insumos_Gestor_Catastral_V1_0", versionConceptOperation30));
				models30Operation.add(new ModelEntity("Submodelo_Insumos_SNR_V1_0", versionConceptOperation30));
				models30Operation.add(new ModelEntity("Submodelo_Integracion_Insumos_V1_0", versionConceptOperation30));

				versionConceptOperation30.setModels(models30Operation);

				versionConceptService.createVersionConcept(versionConceptOperation30);

				VersionConceptEntity versionConceptIntegration30 = new VersionConceptEntity();
				versionConceptIntegration30.setUrl("/opt/storage-microservice-ili/ladm-col/models/3.0");
				versionConceptIntegration30.setVersion(version30);
				versionConceptIntegration30.setConcept(conceptIntegration);

				List<ModelEntity> models30Integration = new ArrayList<ModelEntity>();
				models30Integration
						.add(new ModelEntity("Submodelo_Insumos_Gestor_Catastral_V1_0", versionConceptIntegration30));
				models30Integration.add(new ModelEntity("Submodelo_Insumos_SNR_V1_0", versionConceptIntegration30));
				models30Integration
						.add(new ModelEntity("Submodelo_Integracion_Insumos_V1_0", versionConceptIntegration30));

				versionConceptIntegration30.setModels(models30Integration);

				List<QueryEntity> querys30 = new ArrayList<>();
				querys30.add(new QueryEntity(versionConceptIntegration30, queryIntegration,
						"select snr_p.t_id as snr_predio_juridico, gc.t_id as gc_predio_catastro from {dbschema}.snr_predio_registro as snr_p inner "
								+ "join {dbschema}.gc_predio_catastro as gc on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial "
								+ "and ltrim(snr_p.matricula_inmobiliaria,'0')=trim(gc.matricula_inmobiliaria_catastro) and snr_p.codigo_orip = gc.circulo_registral"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryInsertIntegration,
						"insert into {dbschema}.ini_predio_insumos (gc_predio_catastro, snr_predio_juridico) values ( {cadastre}, {snr})"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryCountSnr,
						"select count(*) from {dbschema}.snr_predio_registro"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryCountCadastre,
						"select count(*) from {dbschema}.gc_predio_catastro"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryCountMatch,
						"select count(*) from {dbschema}.ini_predio_insumos"));

				versionConceptIntegration30.setQuerys(querys30);

				versionConceptService.createVersionConcept(versionConceptIntegration30);

				log.info("The domains 'versions' have been loaded!");
			} catch (Exception e) {
				log.error("Failed to load 'versions' domains");
			}

		}

	}

}
