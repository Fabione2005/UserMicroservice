package com.ms.user.model.generic;

public class BaseResult 
{
	private String message;
	
	public BaseResult(String message) {
		super();
		this.message = message;
	}
	
	public BaseResult() 
	{
		this.message = "Operacion exitosa";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
