package com.ai.st.microservice.ili;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.entities.ModelEntity;
import com.ai.st.microservice.ili.entities.VersionEntity;
import com.ai.st.microservice.ili.services.IVersionService;

@Component
public class StMicroserviceIliApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(StMicroserviceIliApplicationStartup.class);

	@Autowired
	private IVersionService versionService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("ST - Loading Domains ... ");
		this.initVersions();
	}

	public void initVersions() {

		Long countVersions = versionService.getCount();
		if (countVersions == 0) {

			try {

				VersionEntity version294 = new VersionEntity();
				version294.setName("2.9.4");
				version294.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.4");

				List<ModelEntity> models294 = new ArrayList<ModelEntity>();
				models294.add(new ModelEntity("Cartografia_Referencia_V2_9_4", version294));
				models294.add(new ModelEntity("Avaluos_V2_9_4", version294));
				models294.add(new ModelEntity("ISO19107_V1_MAGNABOG", version294));
				models294.add(new ModelEntity("Operacion_V2_9_4", version294));
				models294.add(new ModelEntity("LADM_COL_V1_2", version294));
				models294.add(new ModelEntity("Formulario_Catastro_V2_9_4", version294));
				models294.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_4", version294));
				models294.add(new ModelEntity("Datos_SNR_V2_9_4", version294));
				models294.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_4", version294));

				version294.setModels(models294);
				versionService.createVersion(version294);

				VersionEntity version296 = new VersionEntity();
				version296.setName("2.9.6");
				version296.setUrl("/opt/storage-microservice-ili/ladm-col/models/2.9.6");

				List<ModelEntity> models296 = new ArrayList<ModelEntity>();
				models296.add(new ModelEntity("Cartografia_Referencia_V2_9_6", version296));
				models296.add(new ModelEntity("Avaluos_V2_9_6", version296));
				models296.add(new ModelEntity("Operacion_V2_9_6", version296));
				models296.add(new ModelEntity("LADM_COL_V1_2", version296));
				models296.add(new ModelEntity("Formulario_Catastro_V2_9_6", version296));
				models296.add(new ModelEntity("ISO19107_PLANAS_V1", version296));
				models296.add(new ModelEntity("Datos_Gestor_Catastral_V2_9_6", version296));
				models296.add(new ModelEntity("Datos_SNR_V2_9_6", version296));
				models296.add(new ModelEntity("Datos_Integracion_Insumos_V2_9_6", version296));

				version296.setModels(models296);
				versionService.createVersion(version296);

				log.info("The domains 'versions' have been loaded!");
			} catch (Exception e) {
				log.error("Failed to load 'versions' domains");
			}

		}

	}

}
