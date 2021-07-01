package com.ai.st.microservice.ili;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${iliProcesses.modelsDirectory}")
    private String modelsDirectory;

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

                ConceptEntity conceptBPM = new ConceptEntity();
                conceptBPM.setId(ConceptBusiness.CONCEPT_RECEIPT_FROM_BPM);
                conceptBPM.setName("RECEPCIÓN DE PRODUCTOS A PARTIR DEL BPM");
                conceptService.createConcept(conceptBPM);

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

                QueryTypeEntity typeRegistralToRevision = new QueryTypeEntity();
                typeRegistralToRevision.setId(QueryTypeBusiness.QUERY_TYPE_REGISTRAL_GET_RECORDS_TO_REVISION);
                typeRegistralToRevision.setName("CONSULTA DE REGISTROS PARA ACTUALIZAR FUENTE CABIDA LINDEROS");
                queryTypeService.createQueryType(typeRegistralToRevision);

                QueryTypeEntity typeCountRegistralToRevision = new QueryTypeEntity();
                typeCountRegistralToRevision
                        .setId(QueryTypeBusiness.QUERY_TYPE_COUNT_REGISTRAL_GET_RECORDS_TO_REVISION);
                typeCountRegistralToRevision
                        .setName("COUNT CONSULTA DE REGISTROS PARA ACTUALIZAR FUENTE CABIDA LINDEROS");
                queryTypeService.createQueryType(typeCountRegistralToRevision);

                QueryTypeEntity typeInsertToRevision = new QueryTypeEntity();
                typeInsertToRevision.setId(QueryTypeBusiness.QUERY_TYPE_INSERT_EXTARCHIVO_REVISION);
                typeInsertToRevision.setName("INSERT EN TABLA EXT ARCHIVO");
                queryTypeService.createQueryType(typeInsertToRevision);

                QueryTypeEntity typeSelectToRevision = new QueryTypeEntity();
                typeSelectToRevision.setId(QueryTypeBusiness.QUERY_TYPE_SELECT_EXTARCHIVO_REVISION);
                typeSelectToRevision.setName("SELECT EN TABLA EXT ARCHIVO");
                queryTypeService.createQueryType(typeSelectToRevision);

                QueryTypeEntity typeUpdateToRevision = new QueryTypeEntity();
                typeUpdateToRevision.setId(QueryTypeBusiness.QUERY_TYPE_UPDATE_EXTARCHIVO_REVISION);
                typeUpdateToRevision.setName("UPDATE EN TABLA EXT ARCHIVO");
                queryTypeService.createQueryType(typeUpdateToRevision);

                QueryTypeEntity typeGetPairingTypeIntegration = new QueryTypeEntity();
                typeGetPairingTypeIntegration.setId(QueryTypeBusiness.QUERY_TYPE_GET_PAIRING_TYPE_INTEGRATION);
                typeGetPairingTypeIntegration.setName("OBTENER EL TIPO DE EMPAREJAMIENTO POR ITF CODE");
                queryTypeService.createQueryType(typeGetPairingTypeIntegration);

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

                QueryTypeEntity queryRegistralToRevision = queryTypeService
                        .getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_REGISTRAL_GET_RECORDS_TO_REVISION);

                QueryTypeEntity queryCountRegistralToRevision = queryTypeService
                        .getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_COUNT_REGISTRAL_GET_RECORDS_TO_REVISION);

                QueryTypeEntity queryInsertToRevision = queryTypeService
                        .getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_INSERT_EXTARCHIVO_REVISION);

                QueryTypeEntity querySelectToRevision = queryTypeService
                        .getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_SELECT_EXTARCHIVO_REVISION);

                QueryTypeEntity queryUpdateToRevision = queryTypeService
                        .getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_UPDATE_EXTARCHIVO_REVISION);

                QueryTypeEntity typeGetPairingTypeIntegration = queryTypeService
                        .getQueryTypeById(QueryTypeBusiness.QUERY_TYPE_GET_PAIRING_TYPE_INTEGRATION);

                ConceptEntity conceptOperator = conceptService.getConceptById(ConceptBusiness.CONCEPT_OPERATION);
                ConceptEntity conceptIntegration = conceptService.getConceptById(ConceptBusiness.CONCEPT_INTEGRATION);
                ConceptEntity conceptBPM = conceptService.getConceptById(ConceptBusiness.CONCEPT_RECEIPT_FROM_BPM);

                // version 3.0
                VersionEntity version30 = new VersionEntity();
                version30.setName("3.0");
                version30.setCreatedAt(new Date());
                versionService.createVersion(version30);

                // version 1.0
                VersionEntity version10 = new VersionEntity();
                version10.setName("1.0");
                version10.setCreatedAt(new Date());
                versionService.createVersion(version10);

                // version 1.1
                VersionEntity version11 = new VersionEntity();
                version11.setName("1.1");
                version11.setCreatedAt(new Date());
                versionService.createVersion(version11);

                // version 3.0 - concept operation

                VersionConceptEntity versionConceptOperation30 = new VersionConceptEntity();
                versionConceptOperation30.setUrl(modelsDirectory + "3.0");
                versionConceptOperation30.setVersion(version30);
                versionConceptOperation30.setConcept(conceptOperator);

                List<ModelEntity> models30Operation = new ArrayList<>();
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

                // version 1.0 - concept integration

                VersionConceptEntity versionConceptIntegration30 = new VersionConceptEntity();
                versionConceptIntegration30.setUrl(modelsDirectory + "3.0");
                versionConceptIntegration30.setVersion(version30);
                versionConceptIntegration30.setConcept(conceptIntegration);

                List<ModelEntity> models30Integration = new ArrayList<>();
                models30Integration
                        .add(new ModelEntity("Submodelo_Insumos_Gestor_Catastral_V1_0", versionConceptIntegration30));
                models30Integration.add(new ModelEntity("Submodelo_Insumos_SNR_V1_0", versionConceptIntegration30));
                models30Integration
                        .add(new ModelEntity("Submodelo_Integracion_Insumos_V1_0", versionConceptIntegration30));

                versionConceptIntegration30.setModels(models30Integration);

                List<QueryEntity> query30 = new ArrayList<>();
                query30.add(new QueryEntity(versionConceptIntegration30, queryIntegration,
                        "select snr_p.t_id as snr_predio_juridico, gc.t_id as gc_predio_catastro from {dbschema}.snr_predioregistro as snr_p inner "
                                + "join {dbschema}.gc_prediocatastro as gc on snr_p.numero_predial_nuevo_en_fmi=gc.numero_predial_anterior "
                                + "and ltrim(snr_p.matricula_inmobiliaria,'0')=trim(gc.matricula_inmobiliaria_catastro) and snr_p.codigo_orip = gc.circulo_registral"));
                query30.add(new QueryEntity(versionConceptIntegration30, queryInsertIntegration,
                        "insert into {dbschema}.ini_predioinsumos (gc_predio_catastro, snr_predio_juridico, tipo_emparejamiento) values ( {cadastre}, {snr}, {pairingType})"));
                query30.add(new QueryEntity(versionConceptIntegration30, queryCountSnr,
                        "select count(*) from {dbschema}.snr_predioregistro"));
                query30.add(new QueryEntity(versionConceptIntegration30, queryCountCadastre,
                        "select count(*) from {dbschema}.gc_prediocatastro"));
                query30.add(new QueryEntity(versionConceptIntegration30, queryCountMatch,
                        "select count(*) from {dbschema}.ini_predioinsumos"));
                query30.add(new QueryEntity(versionConceptIntegration30, queryRegistralToRevision,
                        "select " + "	p.t_id, f.t_id as cabidalindero_id, f.ciudad_emisora, "
                                + "	f.ente_emisor, f.fecha_documento, f.numero_documento, "
                                + "	ft.dispname as tipo_documento, (select e.t_id from {dbschema}.extarchivo e where "
                                + "	e.snr_fuentecabidalndros_archivo = f.t_id ) as archivo, "
                                + "	p.numero_predial_nuevo_en_fmi, p.numero_predial_anterior_en_fmi, "
                                + "	p.codigo_orip, p.matricula_inmobiliaria, p.nomenclatura_registro, "
                                + "	p.cabida_linderos from {dbschema}.snr_predioregistro p "
                                + " join {dbschema}.snr_fuentecabidalinderos f on f.t_id = p.snr_fuente_cabidalinderos "
                                + " join {dbschema}.snr_fuentetipo ft on ft.t_id = f.tipo_documento order by "
                                + "	p.t_id limit {limit} offset ({page} * {limit})"));
                query30.add(new QueryEntity(versionConceptIntegration30, queryCountRegistralToRevision,
                        "select count(*) from {dbschema}.snr_predioregistro p join {dbschema}.snr_fuentecabidalinderos f on "
                                + " f.t_id = p.snr_fuente_cabidalinderos "
                                + " join {dbschema}.snr_fuentetipo ft on ft.t_id = f.tipo_documento "));
                versionConceptIntegration30.setQuerys(query30);
                query30.add(new QueryEntity(versionConceptIntegration30, queryInsertToRevision,
                        "INSERT INTO {dbschema}.extarchivo "
                                + " (datos, fecha_entrega, espacio_de_nombres, local_id, snr_fuentecabidalndros_archivo) "
                                + " VALUES('{url}', now(), '{namespace}', {entityId}, {boundaryId})"));
                versionConceptIntegration30.setQuerys(query30);
                query30.add(new QueryEntity(versionConceptIntegration30, querySelectToRevision,
                        "select e.* from {dbschema}.extarchivo e where e.snr_fuentecabidalndros_archivo = {boundaryId}"));
                query30.add(new QueryEntity(versionConceptIntegration30, queryUpdateToRevision,
                        "UPDATE {dbschema}.extarchivo "
                                + "SET datos='{url}', fecha_entrega=now(), espacio_de_nombres='{namespace}', local_id={entityId} "
                                + "WHERE snr_fuentecabidalndros_archivo = {boundaryId}"));
                query30.add(new QueryEntity(versionConceptIntegration30, typeGetPairingTypeIntegration,
                        "select * from {dbschema}.ini_emparejamientotipo where itfcode = {pairingTypeCode}"));

                versionConceptIntegration30.setQuerys(query30);

                versionConceptService.createVersionConcept(versionConceptIntegration30);

                // version 1.0 - concept BPM

                VersionConceptEntity versionConceptBPM10 = new VersionConceptEntity();
                versionConceptBPM10.setUrl(modelsDirectory + "levantamiento/1.0");
                versionConceptBPM10.setVersion(version10);
                versionConceptBPM10.setConcept(conceptBPM);

                List<ModelEntity> models10BPM = new ArrayList<>();
                models10BPM.add(new ModelEntity("Modelo_Aplicacion_LADMCOL_Lev_Cat_V1_0", versionConceptBPM10));
                models10BPM.add(new ModelEntity("Submodelo_Avaluos_V1_0", versionConceptBPM10));
                models10BPM.add(new ModelEntity("Submodelo_Cartografia_Catastral_V1_0", versionConceptBPM10));
                models10BPM.add(new ModelEntity("Submodelo_Insumos_Gestor_Catastral_V1_0", versionConceptBPM10));
                models10BPM.add(new ModelEntity("Submodelo_Insumos_SNR_V1_0", versionConceptBPM10));
                models10BPM.add(new ModelEntity("Submodelo_Integracion_Insumos_V1_0", versionConceptBPM10));
                models10BPM.add(new ModelEntity("LADM_COL_V3_0", versionConceptBPM10));
                models10BPM.add(new ModelEntity("ISO19107_PLANAS_V3_0", versionConceptBPM10));

                versionConceptBPM10.setModels(models10BPM);

                versionConceptService.createVersionConcept(versionConceptBPM10);

                // version 1.1 - concept BPM

                VersionConceptEntity versionConceptBPM11 = new VersionConceptEntity();
                versionConceptBPM11.setUrl(modelsDirectory + "levantamiento/1.1");
                versionConceptBPM11.setVersion(version11);
                versionConceptBPM11.setConcept(conceptBPM);

                List<ModelEntity> models11BPM = new ArrayList<>();

                models11BPM.add(new ModelEntity("Captura_Geo_V0_1", versionConceptBPM11));
                models11BPM.add(new ModelEntity("ISO19107_PLANAS_V3_0", versionConceptBPM11));
                models11BPM.add(new ModelEntity("LADM_COL_V3_0", versionConceptBPM11));
                models11BPM.add(new ModelEntity("Modelo_Aplicacion_LADMCOL_Lev_Cat_V1_1", versionConceptBPM11));
                models11BPM.add(new ModelEntity("Submodelo_Avaluos_V1_1", versionConceptBPM11));
                models11BPM.add(new ModelEntity("Submodelo_Cartografia_Catastral_V1_1", versionConceptBPM11));
                models11BPM.add(new ModelEntity("Submodelo_Insumos_Gestor_Catastral_V1_0", versionConceptBPM11));
                models11BPM.add(new ModelEntity("Submodelo_Insumos_SNR_V1_0", versionConceptBPM11));
                models11BPM.add(new ModelEntity("Submodelo_Integracion_Insumos_V1_0", versionConceptBPM11));

                versionConceptBPM11.setModels(models11BPM);

                versionConceptService.createVersionConcept(versionConceptBPM11);

                log.info("The domains 'versions' have been loaded!");
            } catch (Exception e) {
                log.error("Failed to load 'versions' domains");
            }

        }

    }

}
