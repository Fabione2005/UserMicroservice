package com.ms.user.service

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

import com.ms.user.model.bean.User
import com.ms.user.model.generic.UserToken
import com.ms.user.repository.UserRepository
import com.ms.user.security.TokenUtilComponent
import com.ms.user.security.dto.JwtUtilDto
import com.ms.user.utils.TestUtilsGroovy

import com.ms.user.exception.UserInfoException
import com.ms.user.exception.UserUnauthorizeException
import com.ms.user.exception.UserNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

import spock.lang.Specification

//@SpringBootTest(webEnvironment = NONE)
@SpringBootTest()
class UserServiceTestSpock extends Specification{

	@Autowired
	UserService service

	UserRepository repository = Mock(UserRepository)
	TokenUtilComponent tokenUtilComponent = Mock(TokenUtilComponent)

	def setup() {
		service.setComponents(repository,tokenUtilComponent)
		service.deleteAllUsers()
	}

	def "Tirando Excepcion cuando el email ya esta registrado"() {

		setup:

		def user = TestUtilsGroovy.getUser("Virginia","Vigi@yahoo.com");

		repository.findByEmail(user.getEmail()) >> Optional.of(user)

		when:
		service.addUser(user)

		then:
		def exception = thrown(UserInfoException)
		exception.message == "El correo ya esta registrado"
		0 * repository.save(user)
	}

	def "Traer todos los usuarios"() {
		setup:
		def name = "Roberto"

		def user = TestUtilsGroovy.getUser(name,"Ronnie@yahoo.com")

		def mockUsers = []
		mockUsers << user
		repository.findAll() >> mockUsers

		when:
		def users = service.retriveUsers()

		then:
		users.body.size() == 1
		users.getBody().get(0).name == name
	}
	
	def "Autentificacion fallida por usuario inactivo"() {
		setup:
		
		def userToken = new UserToken("someEmail@hotmail.com","AAAccdda125")
		def user = TestUtilsGroovy.getUser("someName","someEmail@hotmail.com",false)

		repository.findByEmail(userToken.getUserEmail()) >> Optional.of(user)
		repository.findByEmail(userToken.getUserEmail()) >> Optional.of(user)
		when:
		service.loginAuth(userToken)

		then:
		thrown(Exception)
		1 * repository.findByEmail(userToken.getUserEmail())
		0 * tokenUtilComponent.getJWTToken(userToken.getUserEmail())
		0 * repository.save(user)
	}
	
	def "Guardar usuario exitosamente"() {
		setup:
		
		def user = TestUtilsGroovy.getUser("someName","someEmail@hotmail.com",false)
		
		def jwtDto = new JwtUtilDto("someToken","someSobject")

		repository.findByEmail(user.getEmail()) >> Optional.empty()
		tokenUtilComponent.getJwtUtilDto() >> jwtDto

		when:
		def userResponse = service.addUser(user)

		then:
		userResponse.getStatusCode() == HttpStatus.CREATED
		1 * repository.save(user)
	}
	
	def "Guardar usuario falla por clave con formato invalido"() {
		setup:
		
		def user = new User("someName","someEmail","someinvalidpassword",null,null,null,null,false)
		
		def jwtDto = new JwtUtilDto("someToken","someSobject")

		repository.findByEmail(user.getEmail()) >> Optional.empty()
		tokenUtilComponent.getJwtUtilDto() >> jwtDto

		when:
		def userResponse = service.addUser(user)

		then:
		def exception = thrown(UserInfoException)
		exception.message == "La clave debe tener un minimo de una mayuscula, letras minusculas y dos numeros"
		0 * repository.save(user)
	}
	
	def "Actualizar usuario exitosamente"() {
		setup:
		
		def user = TestUtilsGroovy.getUser("someName","someEmail@hotmail.com",false)
		
		def jwtDto = new JwtUtilDto("someToken","someSobject")
		user.setId(UUID.randomUUID())

		repository.findById(user.getId()) >> Optional.of(user)
		repository.findByEmail(user.getEmail()) >> Optional.empty()
		tokenUtilComponent.getJwtUtilDto() >> jwtDto

		when:
		def userResponse = service.updateUser(user)

		then:
		userResponse.getStatusCode() == HttpStatus.OK
		1 * repository.save(user)
	}
	
	def "Actualizar usuario falla por id nulo"() {
		setup:
		
		def user = TestUtilsGroovy.getUser("someName","someEmail@hotmail.com",false)
		
		def jwtDto = new JwtUtilDto("someToken","someSobject")
		

		when:
		def userResponse = service.updateUser(user)

		then:
		def exception = thrown(UserInfoException)
		exception.message == "El campo Id no puede ser nulo"
		0 * repository.save(user)
	}
	
	def "Eliminar usuario exitosamente"() {
		setup:
		
		def id = UUID.randomUUID()
		def user = TestUtilsGroovy.getUser("someName","someEmail@hotmail.com",false)
		
		user.setId(id)

		repository.findById(user.getId()) >> Optional.of(user)

		when:
		def userResponse = service.deleteUser(id)

		then:
		userResponse.getStatusCode() == HttpStatus.OK
		1 * repository.delete(user)
	}
	
	def "Eliminar usuario falla por usuario no encontrado"() {
		setup:
		
		def id = UUID.randomUUID()
		def user = TestUtilsGroovy.getUser("someName","someEmail@hotmail.com",false)
		user.setId(id)
		
		repository.findById(user.getId()) >> Optional.empty()

		when:
		def userResponse = service.deleteUser(id)

		then:
		def exception = thrown(UserNotFoundException)
		exception.message == "Usuario no encontrado"
	}
	
}
