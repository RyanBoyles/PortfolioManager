
package com.portfoliomanager.internals;

import java.util.UUID;

import com.portfoliomanager.domain.User;
import com.portfoliomanager.repository.UserRepository;

public class Login
{
	private String email;
	private String password;
	private boolean validity;
	private String sessionKey;

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

	public boolean isValid()
	{
		return validity;
	}

	public String getSessionKey()
	{
		return sessionKey;
	}
	
	@Override
	public int hashCode()
	{
		return email.hashCode();
	}

	public Login()
	{
		email = "";
		password = "";
		validity = false;
		sessionKey = "";
	}
	
	public Login(Login other)
	{
		email = other.getEmail();
		password = other.getPassword();
		validity = other.isValid();
		sessionKey = other.getSessionKey();
	}
	
	public void checkIfValid(UserRepository userRepo)
	{
		if (userRepo != null)
		{
			User user = userRepo.findOne( new UserID(email) );

			if ((user != null) && (password.equals(user.getPassword())))
			{
				validity = true;
				generateSessionKey();
				return;
			}
		}

		validity = false;
	}
	
	private void generateSessionKey()
	{
		sessionKey = UUID.randomUUID().toString().replaceAll("-", "");
	}

}
