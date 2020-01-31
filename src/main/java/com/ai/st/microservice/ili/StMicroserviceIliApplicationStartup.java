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
import com.ai.st.microservice.ili.entities.ConceptEntity;
import com.ai.st.microservice.ili.entities.ModelEntity;
import com.ai.st.microservice.ili.entities.VersionConceptEntity;
import com.ai.st.microservice.ili.entities.VersionEntity;
import com.ai.st.microservice.ili.services.IConceptService;
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

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("ST - Loading Domains ... ");
		this.initConcepts();
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

	public void initVersions() {

		Long countVersions = versionService.getCount();
		if (countVersions == 0) {

			try {

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
				models296.add(new ModelEntity("Cartografia_Referencia_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Avaluos_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Operacion_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("LADM_COL_V1_2", versionConceptOperation296));
				models296.add(new ModelEntity("Formulario_Catastro_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("ISO19107_PLANAS_V1", versionConceptOperation296));
				models296.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Datos_SNR_V2_9_6", versionConceptOperation296));
				models296.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_6", versionConceptOperation296));

				versionConceptOperation296.setModels(models296);

				versionConceptService.createVersionConcept(versionConceptOperation296);

				log.info("The domains 'versions' have been loaded!");
			} catch (Exception e) {
				log.error("Failed to load 'versions' domains");
			}

		}

	}

}
