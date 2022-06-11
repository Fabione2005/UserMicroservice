package com.ms.user.model.generic;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserToken {
	
	@NotNull(message = "El email no puede estar vacio")
    @Email(message = "El email debe tener un formato valido")
	private String userEmail;
	
	@NotNull(message = "El campo password no puede estar vacio")
	private String password;
	
	
	private String token;
	
	public UserToken(String userEmail, String token) 
	{
		this.userEmail = userEmail;
		this.token = token;
	}
	
	public UserToken() 
	{
		
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	
}
