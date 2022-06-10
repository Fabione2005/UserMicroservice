package com.ms.user.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.ms.user.exception.UserInfoException;
import com.ms.user.exception.UserNotFoundException;
import com.ms.user.exception.UserUnauthorizeException;
import com.ms.user.model.bean.User;
import com.ms.user.model.generic.BaseResult;
import com.ms.user.model.generic.UserToken;
import com.ms.user.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jline.internal.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository daoUser;
	
	//Una hora de vigencia para el token
	private static final long MILLISECONDS_JWT = 3600000;

	@Override
	public ResponseEntity<UserToken> loginAuth(UserToken user) {
		
		User userAthenticated = daoUser.findByEmail(user.getUserEmail()).isPresent() ? daoUser.findByEmail(user.getUserEmail())
				.filter(i -> i.getPassword().equals(user.getPassword()) && i.isActive()).orElseThrow(UserUnauthorizeException::new)
				: daoUser.findByEmail(user.getUserEmail()).orElseThrow(UserUnauthorizeException::new);
		
		String tokenResult = this.getJWTToken(user.getUserEmail());
		
		userAthenticated.setDataWhenLogged(tokenResult);
		
		daoUser.save(userAthenticated);
		
		return ResponseEntity.status(HttpStatus.OK).body(new UserToken(user.getUserEmail(),"Bearer " +tokenResult));
	}

	@Override
	public ResponseEntity<User> addUser(User user) {

		//logger.trace("Agregando usuario " + user.toString());
		Log.info("Agregando usuario " + user.toString());
		daoUser.findByEmail(user.getEmail()).ifPresent(us -> {
			throw new UserInfoException("El correo ya esta registrado", HttpStatus.CONFLICT);
		});
		user.setLocalDatesWhenAdd(LocalDateTime.now());
		daoUser.save(user);
		Log.info("Usuario " + user.getName() + " fue agregado correctamente");
		return ResponseEntity.status(HttpStatus.CREATED).body(user);

	}

	@Override
	public ResponseEntity<BaseResult> updateUser(User user) {

		Log.info("Actualizando usuario " + user.toString());

		User userFound = daoUser.findById(user.getId()).orElseThrow(UserNotFoundException::new);

		Log.info("Usuario encontrado " + userFound.toString());

		if (!userFound.getEmail().equalsIgnoreCase(user.getEmail())) {
			daoUser.findByEmail(user.getEmail()).ifPresent(us -> {
				throw new UserInfoException("El correo ya esta registrado", HttpStatus.CONFLICT);
			});
		}
		user.setModified(LocalDateTime.now());
		daoUser.save(user);

		Log.info("Usuario actualizado exitosamente " + user.toString());
		return ResponseEntity.status(HttpStatus.OK).body(new BaseResult());

	}

	@Override
	public ResponseEntity<BaseResult> deleteUser(UUID id) {

		Log.info("Eliminando usuario con id" + String.valueOf(id));

		User userFound = daoUser.findById(id).orElseThrow(UserNotFoundException::new);

		Log.info("Usuario a eliminar " + userFound.toString());
		daoUser.delete(userFound);
		Log.info("Usuario eliminado exitosamente");
		return ResponseEntity.status(HttpStatus.OK).body(new BaseResult());

	}

	@Override
	public ResponseEntity<User> retriveUserById(UUID id) {

		Log.info("Buscando usuario con id" + String.valueOf(id));

		User userFound = daoUser.findById(id).orElseThrow(UserNotFoundException::new);

		Log.info("Usuario encontrado " + userFound.toString());
		return ResponseEntity.status(HttpStatus.OK).body(userFound);
	}

	@Override
	public ResponseEntity<List<User>> retriveUsers() {

		Log.info("Buscando todos los usuarios en el sistema");

		List<User> usersList = daoUser.findAll();

		Log.info("Retornando todos los usuarios encontrados");
		return ResponseEntity.status(HttpStatus.OK).body(usersList);
	}
	
	@Override
	public ResponseEntity<List<User>> retriveUsersByName(String name) {
		
		Log.info("Buscando usuario con el nombre " + name);

		List<User> userFound = daoUser.findByName(name).orElseThrow(UserNotFoundException::new);

		Log.info("Usuario encontrado " + userFound.toString());
		return ResponseEntity.status(HttpStatus.OK).body(userFound);
	}

	@Override
	public ResponseEntity<BaseResult> deleteAllUsers() {
		
		daoUser.deleteAll();
		
		return ResponseEntity.status(HttpStatus.OK).body(new BaseResult());
	}
	
	private String getJWTToken(String email) {

		Log.info("Generando token para el usuario " + email);

		String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

		String token = Jwts.builder().setId("fabioneJWT").setSubject(email)
				.claim("authorities",
						grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + MILLISECONDS_JWT))
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();

		Log.info("Token creado");
		return token;
	}

	@Override
	public void setRepository(UserRepository repository) {
		
		this.daoUser = repository;
		
	}

	
	
}
