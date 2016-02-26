
package com.portfoliomanager.internals;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class StockID implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// Key Attributes
	private String exchange;
	private String symbol;
	
	public String getExchange()
	{
		return exchange;
	}

	public void setExchange(String exchange)
	{
		this.exchange = exchange;
	}

	public String getSymbol()
	{
		return symbol;
	}

	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + exchange.hashCode();
		result = prime * result + symbol.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) {return false; }
		StockID other = (StockID)obj;
		if (!exchange.equals(other.getExchange())) { return false; }
		if (!symbol.equals(other.getSymbol())) { return false; }
		
		return true;
	}
	
	public StockID()
	{
		exchange = "";
		symbol = "";
	}
	
	public StockID(StockID other)
	{
		exchange = other.getExchange();
		symbol = other.getSymbol();
	}
	
	public StockID(String exchange, String symbol)
	{
		this.exchange = exchange;
		this.symbol = symbol;
	}

}
