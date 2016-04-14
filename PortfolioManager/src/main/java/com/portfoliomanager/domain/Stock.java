
package com.portfoliomanager.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.portfoliomanager.internals.StockID;

@Entity
public class Stock
{
	// Attributes
	private StockID stockID; // Holds key attributes: exchange & symbol
	private String name;
	private BigDecimal lastPrice;
	private BigDecimal priceChange;
	private BigDecimal percentChange;
	
	/*
	// New attributes
	private BigDecimal beta;
	private String sector;
	
	// Possible new attributes?
	private BigDecimal todaysOpen;
	private BigDecimal todaysHigh;
	private BigDecimal todaysLow;
	private BigDecimal fiftyTwoWeekHigh;
	private BigDecimal fiftyTwoWeekLow;
	private BigDecimal priceToEarningRatio;
	private BigDecimal yield;
	*/

	// Relationships
	private Set<Account> accounts = new HashSet<Account>();

	@Id
	public StockID getStockID()
	{
		return stockID;
	}

	public void setStockID(StockID stockID)
	{
		this.stockID = stockID;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public BigDecimal getLastPrice()
	{
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice)
	{
		this.lastPrice = lastPrice;
	}

	public BigDecimal getPriceChange()
	{
		return priceChange;
	}

	public void setPriceChange(BigDecimal priceChange)
	{
		this.priceChange = priceChange;
	}

	public BigDecimal getPercentChange()
	{
		return percentChange;
	}

	public void setPercentChange(BigDecimal percentChange)
	{
		this.percentChange = percentChange;
	}

	@ManyToMany
	public Set<Account> getAccounts()
	{
		return accounts;
	}

	public void setAccounts(Set<Account> accounts)
	{
		this.accounts = accounts;
	}

	public Stock()
	{
		stockID = new StockID();
		name = "";
		lastPrice = new BigDecimal("0");
		priceChange = new BigDecimal("0");
		percentChange = new BigDecimal("0");
	}

	public Stock(Stock other)
	{
		stockID = new StockID(other.getStockID());
		name = other.getName();
		lastPrice = other.getLastPrice();
		priceChange = other.getPriceChange();
		percentChange = other.getPercentChange();
	}

}
