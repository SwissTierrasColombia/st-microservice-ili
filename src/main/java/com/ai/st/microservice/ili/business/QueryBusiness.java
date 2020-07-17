package com.ai.st.microservice.ili.business;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.drivers.PostgresDriver;
import com.ai.st.microservice.ili.dto.ItemRegistralRevisionDto;
import com.ai.st.microservice.ili.dto.QueryResultRegistralRevisionDto;
import com.ai.st.microservice.ili.entities.QueryEntity;
import com.ai.st.microservice.ili.entities.VersionConceptEntity;
import com.ai.st.microservice.ili.entities.VersionEntity;
import com.ai.st.microservice.ili.services.IVersionService;

@Component
public class QueryBusiness {

	@Autowired
	private IVersionService versionService;

	public QueryResultRegistralRevisionDto executeQueryRegistralToRevision(String modelVersion, Long conceptId,
			String host, String database, String port, String schema, String username, String password, int page,
			int limit) {

		QueryResultRegistralRevisionDto queryResultDto = new QueryResultRegistralRevisionDto();
		queryResultDto.setCurrentPage(page);

		List<ItemRegistralRevisionDto> records = new ArrayList<>();

		VersionEntity versionEntity = versionService.getVersionByName(modelVersion);
		if (versionEntity instanceof VersionEntity) {

			VersionConceptEntity versionConcept = versionEntity.getVersionsConcepts().stream()
					.filter(vC -> vC.getConcept().getId().equals(conceptId)).findAny().orElse(null);

			if (versionConcept != null) {

				PostgresDriver connection = new PostgresDriver();

				String urlConnection = "jdbc:postgresql://" + host + ":" + port + "/" + database;
				connection.connect(urlConnection, username, password, "org.postgresql.Driver");

				QueryEntity queryEntity = versionConcept.getQuerys().stream()
						.filter(q -> q.getQueryType().getId()
								.equals(QueryTypeBusiness.QUERY_TYPE_REGISTRAL_GET_RECORDS_TO_REVISION))
						.findAny().orElse(null);

				if (queryEntity != null) {

					String pageString = String.valueOf((page - 1));
					String limitString = String.valueOf(limit);

					String sqlObjects = queryEntity.getQuery().replace("{dbschema}", schema)
							.replace("{page}", pageString).replace("{limit}", limitString);

					ResultSet resultsetObjects = connection.getResultSetFromSql(sqlObjects);

					try {
						while (resultsetObjects.next()) {

							ItemRegistralRevisionDto item = new ItemRegistralRevisionDto();

							String file = resultsetObjects.getString("archivo");
							if (file != null) {
								item.setFileId(Long.parseLong(file));
							}

							String boundarySpace = resultsetObjects.getString("cabida_linderos");
							if (boundarySpace != null) {
								item.setBoundarySpace(boundarySpace);
							}

							String id = resultsetObjects.getString("t_id");
							if (id != null) {
								item.setId(Long.parseLong(id));
							}

							String newFmi = resultsetObjects.getString("numero_predial_nuevo_en_fmi");
							if (newFmi != null) {
								item.setNewFmi(newFmi);
							}

							String nomenclature = resultsetObjects.getString("nomenclatura_registro");
							if (nomenclature != null) {
								item.setNomenclature(nomenclature);
							}

							String oldFmi = resultsetObjects.getString("numero_predial_anterior_en_fmi");
							if (oldFmi != null) {
								item.setOldFmi(oldFmi);
							}

							String orip = resultsetObjects.getString("codigo_orip");
							if (orip != null) {
								item.setOrip(orip);
							}

							String realEstateRegistration = resultsetObjects.getString("matricula_inmobiliaria");
							if (realEstateRegistration != null) {
								item.setRealEstateRegistration(realEstateRegistration);
							}

							String boundarySpaceId = resultsetObjects.getString("cabidalindero_id");
							if (boundarySpaceId != null) {
								item.setBoundaryId(Long.parseLong(boundarySpaceId));
							}

							String issuingCity = resultsetObjects.getString("ciudad_emisora");
							if (issuingCity != null) {
								item.setIssuingCity(issuingCity);
							}

							String issuingEntity = resultsetObjects.getString("ente_emisor");
							if (issuingEntity != null) {
								item.setIssuingEntity(issuingEntity);
							}

							String documentDate = resultsetObjects.getString("fecha_documento");
							if (documentDate != null) {
								item.setDocumentDate(documentDate);
							}

							String documentNumber = resultsetObjects.getString("numero_documento");
							if (documentNumber != null) {
								item.setDocumentNumber(documentNumber);
							}

							String documentType = resultsetObjects.getString("tipo_documento");
							if (documentType != null) {
								item.setDocumentType(documentType);
							}

							records.add(item);

						}
					} catch (SQLException e) {
						System.out.println("Error with query: " + e.getMessage());
					}

					QueryEntity queryCountEntity = versionConcept.getQuerys().stream()
							.filter(q -> q.getQueryType().getId()
									.equals(QueryTypeBusiness.QUERY_TYPE_COUNT_REGISTRAL_GET_RECORDS_TO_REVISION))
							.findAny().orElse(null);
					String sqlCount = queryCountEntity.getQuery().replace("{dbschema}", schema);
					long countRecords = connection.count(sqlCount);
					queryResultDto.setTotalPages((int) countRecords / limit);

					connection.disconnect();

				}

			}

		}

		queryResultDto.setRecords(records);

		return queryResultDto;

	}

	public void executeQueryUpdateToRevision(String modelVersion, Long conceptId, String host, String database,
			String port, String schema, String username, String password, String namespace, String fileUrl,
			Long entityId, Long boundaryId) {

		VersionEntity versionEntity = versionService.getVersionByName(modelVersion);
		if (versionEntity instanceof VersionEntity) {

			VersionConceptEntity versionConcept = versionEntity.getVersionsConcepts().stream()
					.filter(vC -> vC.getConcept().getId().equals(conceptId)).findAny().orElse(null);

			if (versionConcept != null) {

				PostgresDriver connection = new PostgresDriver();

				String urlConnection = "jdbc:postgresql://" + host + ":" + port + "/" + database;
				connection.connect(urlConnection, username, password, "org.postgresql.Driver");

				QueryEntity querySelect = versionConcept.getQuerys().stream().filter(
						q -> q.getQueryType().getId().equals(QueryTypeBusiness.QUERY_TYPE_SELECT_EXTARCHIVO_REVISION))
						.findAny().orElse(null);

				if (querySelect != null) {

					String sqlObjects = querySelect.getQuery().replace("{dbschema}", schema).replace("{boundaryId}",
							boundaryId.toString());

					ResultSet resultsetObjects = connection.getResultSetFromSql(sqlObjects);

					Boolean existFile = false;

					try {
						while (resultsetObjects.next()) {
							existFile = true;
						}
					} catch (SQLException e) {
						System.out.println("Error with query: " + e.getMessage());
					}

					if (existFile) {

						QueryEntity queryUpdate = versionConcept.getQuerys().stream()
								.filter(q -> q.getQueryType().getId()
										.equals(QueryTypeBusiness.QUERY_TYPE_UPDATE_EXTARCHIVO_REVISION))
								.findAny().orElse(null);

						String sqlUpdate = queryUpdate.getQuery().replace("{dbschema}", schema)
								.replace("{url}", fileUrl).replace("{namespace}", namespace)
								.replace("{entityId}", entityId.toString())
								.replace("{boundaryId}", boundaryId.toString());

						connection.insert(sqlUpdate);

					} else {

						QueryEntity queryInsert = versionConcept.getQuerys().stream()
								.filter(q -> q.getQueryType().getId()
										.equals(QueryTypeBusiness.QUERY_TYPE_INSERT_EXTARCHIVO_REVISION))
								.findAny().orElse(null);

						String sqlInsert = queryInsert.getQuery().replace("{dbschema}", schema)
								.replace("{url}", fileUrl).replace("{namespace}", namespace)
								.replace("{entityId}", entityId.toString())
								.replace("{boundaryId}", boundaryId.toString());

						connection.insert(sqlInsert);
					}

					connection.disconnect();
				}

			}

		}

	}

}
