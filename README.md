# ILI MICROSERVICE

Microservice that allows operations with Interlis files.

## Running Microservice

```sh
$ gradle bootRun
```

## Configuration

In the file a you can configure the following properties

```yml
iliProcesses:
  temporalDirectoryPrefix: "ili_process_"
  uploadedFiles: "/opt/storage-microservice-ili/uploads"
  iliDirectory: "/opt/storage-microservice-ili/ladm-col/models/2.9.6"
  iliDirectoryPlugins: "/opt/storage-microservice-ili/ladm-col/models/2.9.6plugins"
  models: "Cartografia_Referencia_V2_9_6;Avaluos_V2_9_6;Operacion_V2_9_6;LADM_COL_V1_2;Formulario_Catastro_V2_9_6;ISO19107_PLANAS_V1;Datos_Gestor_Catastral_V2_9_6;Datos_SNR_V2_9_6;Datos_Integracion_Insumos_V2_9_6"
  srs: "3116"
```

| Property | Description |
| ------ | ------ |
| temporalDirectoryPrefix | Directory prefix where uploaded files will be saved |
| uploadedFiles | Directory where the temporarily loaded files will be saved |
| iliDirectory | Directory where interlist models are stored |
| iliDirectoryPlugins | Directory where interlist plugins are stored |
| models | Model names to be used for ili2db operations |
| srs | Reference system code to use (SRS) |

### How to disable eureka client?

Modify the **enabled** property in st-microservice-ili/src/main/resources/**application.properties** file:

```yml
eureka:
  client:
    enabled: false
```

### How to disable config client?

Modify the **enabled** property in st-microservice-ili/src/main/resources/**bootstrap.properties** file:

```yml
spring:
  application:
    name: st-microservice-ili
  cloud:
    config:
      enabled: false
```

## Swagger Documentation?

See [http://localhost:9005/swagger-ui.html](http://localhost:9005/swagger-ui.html)

## Running Production

### Master Branch

Go to the master branch

```sh
$ git checkout master
```

### Generate jar

```sh
$ gradle build
```

### Create Network Docker

```sh
$ docker network create st
```

### Create image from Dockerfile

```sh
$ docker build -t st-microservice-ili:ursus .
```

### Run Container

```sh
$ docker run -P -t --network st -d st-microservice-ili:ursus
```

### Enter container and create folders
```sh
$ docker exec -it container_name bash
```
```sh
$ mkdir /opt/uploads
```
```sh
$ mkdir /opt/downloads
```
```sh
$ mkdir /opt/models/2.9.6
```
```sh
$ mkdir /opt/models/2.9.6/plugins
```
### Copy models
```sh
$ docker cp /opt/storage-microservice-ili/ladm-col/models/2.9.6/. container_id:/opt/models/2.9.6
```

## License

GNU AFFERO GENERAL PUBLIC LICENSE 
 [Agencia de Implementación - BSF Swissphoto - INCIGE](https://github.com/AgenciaImplementacion/st-microservice-ili/blob/master/LICENSE)