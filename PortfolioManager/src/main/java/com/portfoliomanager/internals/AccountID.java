
package com.portfoliomanager.internals;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class AccountID implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// Key Attributes
	private String company;
	private Long number;

	public String getCompany()
	{
		return company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}
	
	public Long getNumber()
	{
		return number;
	}

	public void setNumber(Long number)
	{
		this.number = number;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + number.hashCode();
		result = prime * result + company.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) {return false; }
		AccountID other = (AccountID)obj;
		if (!company.equals(other.getCompany())) { return false; }
		if (number != other.getNumber()) { return false; }
		
		return true;
	}
	
	public AccountID()
	{
		company = "";
		number = -1L;
	}
	
	public AccountID(AccountID other)
	{
		company = other.getCompany();
		number = other.getNumber();
	}
	
	public AccountID(String company, Long number)
	{
		this.company = company;
		this.number = number;
	}

}
