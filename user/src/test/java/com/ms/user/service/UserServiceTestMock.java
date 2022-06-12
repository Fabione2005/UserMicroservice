package com.ms.user.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.ms.user.exception.UserInfoException;
import com.ms.user.exception.UserNotFoundException;
import com.ms.user.exception.UserUnauthorizeException;
import com.ms.user.model.bean.User;
import com.ms.user.model.generic.UserToken;
import com.ms.user.repository.UserRepository;
import com.ms.user.security.TokenUtilComponent;
import com.ms.user.security.dto.JwtUtilDto;
import com.ms.user.util.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTestMock {

	@Autowired
	private UserService service;

	@MockBean
	private UserRepository repository;
	
	@MockBean
	private TokenUtilComponent tokenUtilComponent;

	@Test
	public void getUsersTest() {
		when(repository.findAll()).thenReturn(Stream
				.of(TestUtils.getUser("Jhoan", "Jhoan123@yahoo.cl")).collect(Collectors.toList()));
		assertEquals(1, service.retriveUsers().getBody().size());
	}
	
	@Test
	public void saveUserTest() {
		User user = TestUtils.getUser("Samuel", "Samuel@jjj.cl");
		JwtUtilDto jwt = new JwtUtilDto("someToken","sobject");
		when(tokenUtilComponent.getJwtUtilDto()).thenReturn(jwt);
		when(repository.save(user)).thenReturn(user);
		assertEquals(user, service.addUser(user).getBody());
	}
	
	@Test(expected = UserNotFoundException.class)
	public void UserCantBeDeleteTest(){
		User user = TestUtils.getUser("Samuel", "Samuel@jjj.cl");
		assertEquals(service.deleteUser(user.getId()), new UserNotFoundException());
	}
	
	@Test
	public void findUsersByNameTest(){
		final String name = "Daniela";
		when(repository.findByName(name)).thenReturn(Optional.of(Stream.of(TestUtils.getUser("Daniela", "Danielita@gmail.com"),TestUtils.getUser("Daniela", "Dani5533@gmail.com")
				).collect(Collectors.toList())));
		assertEquals(service.retriveUsersByName(name).getBody().size(), 2);
	}
	
	@Test(expected = UserInfoException.class)
	public void emailAlreadyRegisteredTest() 
	{
		User user = TestUtils.getUser("Samuel", "Samuel@jjj.cl");
		when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(TestUtils.getUser("Samueleano", "Samuel@jjj.cl")));
		service.addUser(user);
	}
	
	@Test
	public void updateUserTest() 
	{
		User user = TestUtils.getUser("Samuel", "Samuel@hotmail.com");
		user.setId(UUID.randomUUID());
		when(repository.findById(user.getId())).thenReturn(Optional.of(user));
		JwtUtilDto jwt = new JwtUtilDto("someToken","sobject");
		when(tokenUtilComponent.getJwtUtilDto()).thenReturn(jwt);
		service.updateUser(user);
		verify(repository, times(1)).findById(user.getId());
		verify(repository, times(1)).save(user);
	}
	
	@Test(expected = UserUnauthorizeException.class)
	public void userAuthTest() 
	{
		UserToken user = TestUtils.getUserAuth("Paulina@yahoo.com", "AAAccdda125");
		when(repository.findByEmail(user.getUserEmail())).thenReturn(Optional.empty());
		assertEquals(service.loginAuth(user), new UserUnauthorizeException());
	}
	
	
}
