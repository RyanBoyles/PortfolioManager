
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
	private BigDecimal todaysOpen;
	private BigDecimal todaysHigh;
	private BigDecimal todaysLow;
	private BigDecimal fiftyTwoWeekHigh;
	private BigDecimal fiftyTwoWeekLow;
	private BigDecimal priceEarningRatio;
	private BigDecimal yield;
	private BigDecimal beta;
	
	/*
	// Possible new attributes?
	private String sector;
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
	
	public BigDecimal getTodaysOpen()
	{
		return todaysOpen;
	}

	public void setTodaysOpen(BigDecimal todaysOpen)
	{
		this.todaysOpen = todaysOpen;
	}

	public BigDecimal getTodaysHigh()
	{
		return todaysHigh;
	}

	public void setTodaysHigh(BigDecimal todaysHigh)
	{
		this.todaysHigh = todaysHigh;
	}

	public BigDecimal getTodaysLow()
	{
		return todaysLow;
	}

	public void setTodaysLow(BigDecimal todaysLow)
	{
		this.todaysLow = todaysLow;
	}

	public BigDecimal getFiftyTwoWeekHigh()
	{
		return fiftyTwoWeekHigh;
	}

	public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh)
	{
		this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
	}

	public BigDecimal getFiftyTwoWeekLow()
	{
		return fiftyTwoWeekLow;
	}

	public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow)
	{
		this.fiftyTwoWeekLow = fiftyTwoWeekLow;
	}

	public BigDecimal getPriceEarningRatio()
	{
		return priceEarningRatio;
	}

	public void setPriceEarningRatio(BigDecimal priceEarningRatio)
	{
		this.priceEarningRatio = priceEarningRatio;
	}

	public BigDecimal getYield()
	{
		return yield;
	}

	public void setYield(BigDecimal yield)
	{
		this.yield = yield;
	}

	public BigDecimal getBeta()
	{
		return beta;
	}

	public void setBeta(BigDecimal beta)
	{
		this.beta = beta;
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
		todaysOpen = new BigDecimal("0");
		todaysHigh = new BigDecimal("0");
		todaysLow = new BigDecimal("0");
		fiftyTwoWeekHigh = new BigDecimal("0");
		fiftyTwoWeekLow = new BigDecimal("0");
		priceEarningRatio = new BigDecimal("0");
		yield = new BigDecimal("0");
		beta = new BigDecimal("0");
	}

	public Stock(Stock other)
	{
		stockID = new StockID(other.getStockID());
		name = other.getName();
		lastPrice = other.getLastPrice();
		priceChange = other.getPriceChange();
		percentChange = other.getPercentChange();
		todaysOpen = other.getTodaysOpen();
		todaysHigh = other.getTodaysHigh();
		todaysLow = other.getTodaysLow();
		fiftyTwoWeekHigh = other.getFiftyTwoWeekHigh();
		fiftyTwoWeekLow = other.getFiftyTwoWeekLow();
		priceEarningRatio = other.getPriceEarningRatio();
		yield = other.getYield();
		beta = other.getBeta();
	}

}
