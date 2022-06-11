package com.ms.user.security.dto;

import lombok.ToString;


@ToString
public class JwtUtilDto {

	private String token;
	
	private String subject;
	
	public JwtUtilDto(String token, String subject) 
	{
		this.token = token;
		this.subject = subject;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public JwtUtilDto() 
	{
		
	}
	
}
