package com.ms.user.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ms.user.model.bean.User;
import com.ms.user.model.generic.BaseResult;
import com.ms.user.model.generic.UserToken;
import com.ms.user.repository.UserRepository;

public interface UserService 
{
	ResponseEntity<UserToken> loginAuth(UserToken user);
	ResponseEntity<User> addUser(User user);
	ResponseEntity<BaseResult> updateUser(User user);
	ResponseEntity<BaseResult> deleteUser(UUID id);
	ResponseEntity<User> retriveUserById(UUID id);
	ResponseEntity<List<User>> retriveUsers();
	ResponseEntity<List<User>> retriveUsersByName(String name);
	ResponseEntity<BaseResult> deleteAllUsers();
	void setRepository(UserRepository repository);
}
