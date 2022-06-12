package com.ms.user.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ms.user.model.bean.Phone;
import com.ms.user.model.bean.User;
import com.ms.user.model.generic.UserToken;



public class TestUtils {

	public static User getUser(String name, String email) {
		LocalDateTime currentTime = LocalDateTime.now();
		List<Phone> tempList = new ArrayList<>();
		tempList.add(new Phone(65464654L, 542L, 47L));

		User user = new User(name, email, "AAcccddser4256", tempList, currentTime, currentTime, currentTime, true);
		return user;
	}
	
	public static UserToken getUserAuth(String email, String password) 
	{
		return new UserToken(email,password);
	}
	
}
