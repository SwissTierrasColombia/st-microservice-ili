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
						"select snr_p.t_id as snr_predio_juridico, gc.t_id as gc_predio_catastro from {dbschema}.snr_predioregistro as snr_p inner "
								+ "join {dbschema}.gc_prediocatastro as gc on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial_anterior "
								+ "and ltrim(snr_p.matricula_inmobiliaria,'0')=trim(gc.matricula_inmobiliaria_catastro) and snr_p.codigo_orip = gc.circulo_registral"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryInsertIntegration,
						"insert into {dbschema}.ini_predioinsumos (gc_predio_catastro, snr_predio_juridico) values ( {cadastre}, {snr})"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryCountSnr,
						"select count(*) from {dbschema}.snr_predioregistro"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryCountCadastre,
						"select count(*) from {dbschema}.gc_prediocatastro"));
				querys30.add(new QueryEntity(versionConceptIntegration30, queryCountMatch,
						"select count(*) from {dbschema}.ini_predioinsumos"));

				versionConceptIntegration30.setQuerys(querys30);

				versionConceptService.createVersionConcept(versionConceptIntegration30);

				log.info("The domains 'versions' have been loaded!");
			} catch (Exception e) {
				log.error("Failed to load 'versions' domains");
			}

		}

	}

}
