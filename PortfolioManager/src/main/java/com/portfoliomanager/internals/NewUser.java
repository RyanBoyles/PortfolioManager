
package com.portfoliomanager.internals;

import com.portfoliomanager.domain.User;

public class NewUser
{
	private String name;
	private String email;
	private String password;
	private String verifiedPassword;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getVerifiedPassword()
	{
		return verifiedPassword;
	}

	public void setVerifiedPassword(String verifiedPassword)
	{
		this.verifiedPassword = verifiedPassword;
	}
	
	public NewUser()
	{
		name = "";
		email = "";
		password = "";
		verifiedPassword = "";
	}
	
	public NewUser(User existingUser)
	{
		name = existingUser.getName();
		email = existingUser.getUserID().getEmail();
		password = "";
		verifiedPassword = "";
	}

}
