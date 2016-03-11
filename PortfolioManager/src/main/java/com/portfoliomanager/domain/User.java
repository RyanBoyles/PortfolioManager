
package com.portfoliomanager.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.portfoliomanager.internals.UserID;

@Entity
public class User
{
	// Attributes
	private UserID userID; // Holds key attribute: email
	private String password;
	private String name;

	// Relationships
	private Set<Account> accounts = new HashSet<Account>();

	@Id
	public UserID getUserID()
	{
		return userID;
	}

	public void setUserID(UserID userID)
	{
		this.userID = userID;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
	public Set<Account> getAccounts()  // TODO: MAYBE CONVERT THIS TO NATIVE SQL AND CONVERT THE LIST TO A SET. MUST IMPLEMENT .HASHCODE() AND .EQUALS IN THE ACCOUNT CLASS TO DO THIS. REFERENCE "http://stackoverflow.com/questions/1429860/easiest-way-to-convert-a-list-to-a-set-java".
	{
		return accounts;
	}

	public void setAccounts(Set<Account> accounts)
	{
		this.accounts = accounts;
	}
	
	public User()
	{
		userID = new UserID();
		password = "";
		name = "";
	}
	
	public User(User other)
	{
		userID = new UserID(other.getUserID());
		password = other.getPassword();
		name = other.getName();
	}

}
