# Microservicio - Usuario

Microservicio con la responsabilidad de gestionar los usuarios del sistema, tanto agregarlos, modificarlos, consultarlos y eliminarlos.

##  Operaciones
Detalla las operaciones que se pueden explotar a través del microservicio. 

| Método | Operación                   | Descripción Capacidad                                                      |
| GET    | /v2/api-docs                | Con esta operacion se obtiene la documentacion de las apis en formato json |                                                                   
| GET    | /user/auth            	   | Con esta operacion se obtiene el token de autenticacion                    |
| POST   | /user/add                   | Con esta operacion se agrega a un nuevo usuario en el sistema              |
| GET    | /user/list                  | Con esta operacion se obtiene la lista de usuarios que viven en el sistema |
| PUT    | /user/update                | Con esta operacion se modifica a un usuario existente en el sistema        |
| DELETE | delete/{id}                 | Con esta operacion se elimina el usuario del sistema                       |

## Tech stack

- Java 8
- Springboot
- Gradle
- Junit & Mockito
- Groovy & Spock
- JPA
- H2
- JWT
  
### Repositorio de Datos

| Repositorio| Descripción                                                                  |
| ------     | ------                                                                       |
| H2      	 | Base de Datos en memoria que permite almacenar los usuarios en el sistema  	|


### Compilación

1. Puedes correr el API desde tu IDE o desde el compilado del proyecto luego de hacer build con gradle ubicado en .builds/libs. <br><br>

2. Para llamar al endpoint del token se deben utilizar los datos(email y clave) ya guardados en el archivo "src/main/resources/data.sql" el cual guarda los datos en la H2	al correr la app. De igual manera dejo aca las creedenciales: "Manuelito@yahoo.com", "AAcisco43234".<br><br>

3. Para los test utilize JUnit para correr las pruebas tanto de java como de groovy(Spock).  

## Versiones
 
### Versión 1.0.0
 - Creacion de endpoint para la documentacion con Swagger
 - Creación de endpoint para obtener el token de autenticacion
 - Creacion de enpoint para crear un nuevo usuario
 - Creacion de endpoint para listar los usuarios existentes
 - Creacion de endpoint para actualizar usuario existente
 - Creacion de endpoint para eliminar un usuario