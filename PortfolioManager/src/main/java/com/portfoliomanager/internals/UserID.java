
package com.portfoliomanager.internals;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class UserID implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// Key Attributes
	private String email;

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}
	
	@Override
	public int hashCode()
	{
		/*
		final int prime = 31;
		int result = 1;
		result = prime * result + email.hashCode();
		return result;
		*/
		return email.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) {return false; }
		UserID other = (UserID)obj;
		if (!email.equals(other.getEmail())) { return false; }
		
		return true;
	}
	
	public UserID()
	{
		email = "";
	}
	
	public UserID(UserID other)
	{
		email = other.getEmail();
	}
	
	public UserID(String email)
	{
		this.email = email;
	}
	
}
