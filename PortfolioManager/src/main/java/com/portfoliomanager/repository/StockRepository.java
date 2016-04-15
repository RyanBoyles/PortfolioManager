
package com.portfoliomanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.portfoliomanager.domain.Stock;
import com.portfoliomanager.internals.StockID;

public interface StockRepository extends JpaRepository<Stock, StockID>
{
	@Query(value="SELECT * FROM Stock s WHERE s.exchange = :exchange AND s.symbol = :symbol", nativeQuery = true)
	public List<Stock> findStock(@Param("exchange") String exchange, @Param("symbol") String symbol);
	
	
	
	
	// THE FIVE QUERIES BELOW NEED TO BE CHANGED TO SELECT ANY NEW STOCK ATTRIBUTES THAT GET ADDED.
	
	@Query(value = "SELECT s.exchange, s.symbol, s.name, s.lastPrice, s.priceChange, s.percentChange, s.todaysOpen, s.todaysHigh, s.todaysLow, s.fiftyTwoWeekHigh, s.fiftyTwoWeekLow, s.priceEarningRatio, s.yield FROM Stock s, Stock_Account sa WHERE sa.accounts_company = :company AND sa.accounts_number = :number AND sa.stocks_exchange = :exchange AND sa.stocks_symbol = :symbol AND s.exchange = sa.stocks_exchange AND s.symbol = sa.stocks_symbol", nativeQuery = true)
	public List<Stock> findStockInAccount(@Param("company") String company, @Param("number") Long number, @Param("exchange") String exchange, @Param("symbol") String symbol);
	
	// THIS IS A JOIN, MAYBE.
	@Query(value = "SELECT s.exchange, s.symbol, s.name, s.lastPrice, s.priceChange, s.percentChange, s.todaysOpen, s.todaysHigh, s.todaysLow, s.fiftyTwoWeekHigh, s.fiftyTwoWeekLow, s.priceEarningRatio, s.yield FROM Stock s, Stock_Account sa WHERE sa.accounts_company = :company AND sa.accounts_number = :number AND s.exchange = sa.stocks_exchange AND s.symbol = sa.stocks_symbol", nativeQuery = true)
	public List<Stock> findAllStocksInAccount(@Param("company") String company, @Param("number") Long number);
	
	@Query(value = "SELECT s.exchange, s.symbol, s.name, s.lastPrice, s.priceChange, s.percentChange, s.todaysOpen, s.todaysHigh, s.todaysLow, s.fiftyTwoWeekHigh, s.fiftyTwoWeekLow, s.priceEarningRatio, s.yield FROM Stock s", nativeQuery = true)
	public List<Stock> findAllStocks();
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE Stock s SET s.name = :name, s.lastPrice = :lastPrice, s.priceChange = :priceChange, s.percentChange = :percentChange, s.todaysOpen = :todaysOpen, s.todaysHigh = :todaysHigh, s.todaysLow = :todaysLow, s.fiftyTwoWeekHigh = :fiftyTwoWeekHigh, s.fiftyTwoWeekLow = :fiftyTwoWeekLow, s.priceEarningRatio = :priceEarningRatio, s.yield = :yield WHERE s.symbol = :symbol AND s.exchange = :exchange", nativeQuery = true)
	public void updateStock(@Param("symbol") String symbol, @Param("exchange") String exchange, @Param("name") String name, @Param("lastPrice") double lastPrice, @Param("priceChange") double priceChange, @Param("percentChange") double percentChange, @Param("todaysOpen") double todaysOpen, @Param("todaysHigh") double todaysHigh, @Param("todaysLow") double todaysLow, @Param("fiftyTwoWeekHigh") double fiftyTwoWeekHigh, @Param("fiftyTwoWeekLow") double fiftyTwoWeekLow, @Param("priceEarningRatio") double priceEarningRatio, @Param("yield") double yield);
	
	@Query(value = "SELECT s.exchange, s.symbol, s.name, s.lastPrice, s.priceChange, s.percentChange, s.todaysOpen, s.todaysHigh, s.todaysLow, s.fiftyTwoWeekHigh, s.fiftyTwoWeekLow, s.priceEarningRatio, s.yield FROM Stock s WHERE s.exchange = :exchange AND s.symbol <> :symbol AND s.lastPrice BETWEEN :p_low AND :p_high", nativeQuery = true)
	public List<Stock> findSimilar(@Param("exchange") String exchange, @Param("symbol") String symbol, @Param("p_low") double p_low, @Param("p_high") double p_high);
	
	
	
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO Stock(exchange, symbol) VALUES (:exchange, :symbol)", nativeQuery = true)
	public void addStock(@Param("exchange") String exchange, @Param("symbol") String symbol);
	
	//@Modifying
	//@Transactional
	//@Query(value = "DELETE FROM Stock WHERE NOT EXISTS(SELECT * FROM Stock_Account sa WHERE sa.stocks_exchange = exchange AND sa.stocks_symbol = symbol)", nativeQuery = true)
	//public void removeStocksHeldInNoAccounts();
}
