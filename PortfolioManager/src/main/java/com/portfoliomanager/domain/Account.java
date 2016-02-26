
package com.portfoliomanager.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.portfoliomanager.internals.AccountID;

@Entity
public class Account
{
	// Attributes
	private AccountID accountID; // Holds key attributes: company & number
	private String label;

	// Relationships
	private User user;
	private Set<Stock> stocks = new HashSet<Stock>();

	@Id
	public AccountID getAccountID()
	{
		return accountID;
	}

	public void setAccountID(AccountID accountID)
	{
		this.accountID = accountID;
	}
	
	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	@ManyToOne
	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="accounts")
	public Set<Stock> getStocks()
	{
		return stocks;
	}

	public void setStocks(Set<Stock> stocks)
	{
		this.stocks = stocks;
	}
	
	public Account()
	{
		accountID = new AccountID();
		label = "";
	}
	
	public Account(Account other)
	{
		accountID = new AccountID(other.getAccountID());
		label = other.getLabel();
	}

}
