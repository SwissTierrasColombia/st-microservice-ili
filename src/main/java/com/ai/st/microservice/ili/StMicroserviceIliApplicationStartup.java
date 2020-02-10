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

				VersionEntity version294 = new VersionEntity();
				version294.setName("2.9.4");
				version294.setCreatedAt(new Date());
				versionService.createVersion(version294);

				VersionConceptEntity versionConceptOperation294 = new VersionConceptEntity();
				versionConceptOperation294.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.4");
				versionConceptOperation294.setVersion(version294);
				versionConceptOperation294.setConcept(conceptOperator);

				List<ModelEntity> models294 = new ArrayList<ModelEntity>();
				models294.add(new ModelEntity("Cartografia_Referencia_V2_9_4", versionConceptOperation294));
				models294.add(new ModelEntity("Avaluos_V2_9_4", versionConceptOperation294));
				models294.add(new ModelEntity("ISO19107_V1_MAGNABOG", versionConceptOperation294));
				models294.add(new ModelEntity("Operacion_V2_9_4", versionConceptOperation294));
				models294.add(new ModelEntity("LADM_COL_V1_2", versionConceptOperation294));
				models294.add(new ModelEntity("Formulario_Catastro_V2_9_4", versionConceptOperation294));
				models294.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_4", versionConceptOperation294));
				models294.add(new ModelEntity("Datos_SNR_V2_9_4", versionConceptOperation294));
				models294.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_4", versionConceptOperation294));

				versionConceptOperation294.setModels(models294);

				List<QueryEntity> querys294 = new ArrayList<>();
				querys294.add(new QueryEntity(versionConceptOperation294, queryIntegration,
						"select snr_p.t_id as snr_predio_juridico, gc.t_id as gc_predio_catastro from {dbschema}.snr_predio_juridico as snr_p inner"
						+ " join {dbschema}.gc_predio_catastro as gc on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial and "
						+ "ltrim(snr_p.matricula_inmobiliaria,'0')=trim(gc.matricula_inmobiliaria_catastro) and snr_p.codigo_orip = gc.circulo_registral"));
				querys294.add(new QueryEntity(versionConceptOperation294, queryInsertIntegration,
						"insert into {dbschema}.ini_predio_insumos (gc_predio_catastro, snr_predio_juridico) values ( {cadastre}, {snr})"));
				querys294.add(new QueryEntity(versionConceptOperation294, queryCountSnr,
						"select count(*) from {dbschema}.snr_predio_juridico"));
				querys294.add(new QueryEntity(versionConceptOperation294, queryCountCadastre,
						"select count(*) from {dbschema}.gc_predio_catastro"));
				querys294.add(new QueryEntity(versionConceptOperation294, queryCountMatch,
						"select count(*) from {dbschema}.ini_predio_insumos"));
				versionConceptOperation294.setQuerys(querys294);

				versionConceptService.createVersionConcept(versionConceptOperation294);

				VersionEntity version296 = new VersionEntity();
				version296.setName("2.9.6");
				version296.setCreatedAt(new Date());
				versionService.createVersion(version296);

				VersionConceptEntity versionConceptOperation296 = new VersionConceptEntity();
				versionConceptOperation296.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.6");
				versionConceptOperation296.setVersion(version296);
				versionConceptOperation296.setConcept(conceptOperator);

				List<ModelEntity> models296 = new ArrayList<ModelEntity>();
				models296.add(new ModelEntity("ANT_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Cartografia_Referencia_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Avaluos_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Operacion_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("LADM_COL_V1_3", versionConceptOperation296));
				models296.add(new ModelEntity("Formulario_Catastro_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("ISO19107_PLANAS_V1", versionConceptOperation296));
				models296.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Datos_SNR_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_6", versionConceptOperation296));

				versionConceptOperation296.setModels(models296);

				List<QueryEntity> querys296 = new ArrayList<>();
				querys296.add(new QueryEntity(versionConceptOperation296, queryIntegration,
						"select snr_p.t_id as snr_predio_juridico, gc.t_id as gc_predio_catastro from {dbschema}.snr_predio_registro as snr_p inner "
						+ "join {dbschema}.gc_predio_catastro as gc on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial "
						+ "and snr_p.codigo_orip = gc.circulo_registral"));
				querys296.add(new QueryEntity(versionConceptOperation296, queryInsertIntegration,
						"insert into {dbschema}.ini_predio_insumos (gc_predio_catastro, snr_predio_juridico) values ( {cadastre}, {snr})"));
				querys296.add(new QueryEntity(versionConceptOperation296, queryCountSnr,
						"select count(*) from {dbschema}.snr_predio_registro"));
				querys296.add(new QueryEntity(versionConceptOperation296, queryCountCadastre,
						"select count(*) from {dbschema}.gc_predio_catastro"));
				querys296.add(new QueryEntity(versionConceptOperation296, queryCountMatch,
						"select count(*) from {dbschema}.ini_predio_insumos"));
				versionConceptOperation296.setQuerys(querys296);

				versionConceptService.createVersionConcept(versionConceptOperation296);

				log.info("The domains 'versions' have been loaded!");
			} catch (Exception e) {
				log.error("Failed to load 'versions' domains");
			}

		}

	}

}
