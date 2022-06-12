package com.ms.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ms.user.exception.UserInfoException;
import com.ms.user.exception.UserNotFoundException;
import com.ms.user.exception.UserUnauthorizeException;
import com.ms.user.logger.CommonLogger;
import com.ms.user.model.bean.User;
import com.ms.user.model.generic.BaseResult;
import com.ms.user.model.generic.UserToken;
import com.ms.user.repository.UserRepository;
import com.ms.user.security.TokenUtilComponent;
import com.ms.user.security.dto.JwtUtilDto;

import jline.internal.Log;


@Service
public class UserServiceImpl extends CommonLogger implements UserService {

	@Autowired
	private UserRepository daoUser;
	
	@Autowired
	private TokenUtilComponent tokenUtilComponent;
	
	@Value("${validate.password.regex}")
	private String passwordRegex;
	
	@Value("${validate.password.message}")
	private String passwordMessage;

	@Override
	public ResponseEntity<UserToken> loginAuth(UserToken user) {
		
		User userAthenticated = daoUser.findByEmail(user.getUserEmail()).isPresent() ? daoUser.findByEmail(user.getUserEmail())
				.filter(i -> i.getPassword().equals(user.getPassword()) && i.isActive()).orElseThrow(UserUnauthorizeException::new)
				: daoUser.findByEmail(user.getUserEmail()).orElseThrow(UserUnauthorizeException::new);
		
		String tokenResult = tokenUtilComponent.getJWTToken(user.getUserEmail());
		
		userAthenticated.setDataWhenLogged(tokenResult);
		
		daoUser.save(userAthenticated);
		
		return ResponseEntity.status(HttpStatus.OK).body(new UserToken(user.getUserEmail(),"Bearer " +tokenResult));
	}

	@Override
	public ResponseEntity<User> addUser(User user) {

		logger.info("Agregando usuario " + user.toString());
		daoUser.findByEmail(user.getEmail()).ifPresent(us -> {
			throw new UserInfoException("El correo ya esta registrado", HttpStatus.CONFLICT);
		});
		
		this.validatePassword(user.getPassword());
		
		JwtUtilDto jwtUtilDto = tokenUtilComponent.getJwtUtilDto();
		
		user.setLocalDatesWhenAdd(LocalDateTime.now());
		user.setToken(jwtUtilDto.getToken());
		user.setCreatedBy(jwtUtilDto.getSubject());
		daoUser.save(user);
		logger.info("Usuario " + user.getName() + " fue agregado correctamente");
		return ResponseEntity.status(HttpStatus.CREATED).body(user);

	}

	@Override
	public ResponseEntity<BaseResult> updateUser(User user) {

		if(user.getId() == null || user.getId().equals(""))
			throw new UserInfoException("El campo Id no puede ser nulo", HttpStatus.BAD_REQUEST);
		
		this.validatePassword(user.getPassword());
		
		logger.trace("Actualizando usuario " + user.toString());

		User userFound = daoUser.findById(user.getId()).orElseThrow(UserNotFoundException::new);

		logger.info("Usuario encontrado " + userFound.toString());

		if (!userFound.getEmail().equalsIgnoreCase(user.getEmail())) {
			daoUser.findByEmail(user.getEmail()).ifPresent(us -> {
				throw new UserInfoException("El correo ya esta registrado", HttpStatus.CONFLICT);
			});
		}
		
		JwtUtilDto jwtUtilDto = tokenUtilComponent.getJwtUtilDto();
		
		user.setModified(LocalDateTime.now());
		user.setToken(jwtUtilDto.getToken());
		user.setUpdatedBy(jwtUtilDto.getSubject());
		daoUser.save(user);

		logger.info("Usuario actualizado exitosamente " + user.toString());
		return ResponseEntity.status(HttpStatus.OK).body(new BaseResult());

	}

	@Override
	public ResponseEntity<BaseResult> deleteUser(UUID id) {

		logger.trace("Eliminando usuario con id" + String.valueOf(id));

		User userFound = daoUser.findById(id).orElseThrow(UserNotFoundException::new);

		logger.info("Usuario a eliminar " + userFound.toString());
		daoUser.delete(userFound);
		logger.info("Usuario eliminado exitosamente");
		return ResponseEntity.status(HttpStatus.OK).body(new BaseResult());

	}

	@Override
	public ResponseEntity<User> retriveUserById(UUID id) {

		logger.trace("Buscando usuario con id" + String.valueOf(id));

		User userFound = daoUser.findById(id).orElseThrow(UserNotFoundException::new);

		logger.info("Usuario encontrado " + userFound.toString());
		return ResponseEntity.status(HttpStatus.OK).body(userFound);
	}

	@Override
	public ResponseEntity<List<User>> retriveUsers() {

		logger.trace("Buscando todos los usuarios en el sistema");

		List<User> usersList = daoUser.findAll();

		logger.trace("Retornando todos los usuarios encontrados");
		return ResponseEntity.status(HttpStatus.OK).body(usersList);
	}
	
	@Override
	public ResponseEntity<List<User>> retriveUsersByName(String name) {
		
		logger.trace("Buscando usuario con el nombre " + name);

		List<User> userFound = daoUser.findByName(name).orElseThrow(UserNotFoundException::new);

		logger.info("Usuario encontrado " + userFound.toString());
		return ResponseEntity.status(HttpStatus.OK).body(userFound);
	}

	@Override
	public ResponseEntity<BaseResult> deleteAllUsers() {
		
		daoUser.deleteAll();
		
		return ResponseEntity.status(HttpStatus.OK).body(new BaseResult());
	}
	

	@Override
	public void setComponents(UserRepository repository,TokenUtilComponent tokenUtilComponent) {
		
		this.daoUser = repository;
		this.tokenUtilComponent = tokenUtilComponent;
		
	}
	
	
	private void validatePassword(String password){

		Log.info("validando clave");
		
	    Pattern pattern = Pattern.compile(passwordRegex);
	    Matcher matcher = pattern.matcher(password);
	    
	    if(!matcher.matches())
	    	throw new UserInfoException(passwordMessage, HttpStatus.BAD_REQUEST);
	    
	    Log.info("Clave valida");
	}

}
