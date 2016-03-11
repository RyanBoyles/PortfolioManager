
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
	@Query(value = "SELECT * FROM Stock_Account sa WHERE sa.accounts_company = :company AND sa.accounts_number = :number AND sa.stocks_exchange = :exchange AND sa.stocks_symbol = :symbol", nativeQuery = true)
	public List<Stock> findStockInAccount(@Param("company") String company, @Param("number") Long number, @Param("exchange") String exchange, @Param("symbol") String symbol);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO Stock(exchange, symbol) VALUES (:exchange, :symbol)", nativeQuery = true)
	public void addStock(@Param("exchange") String exchange, @Param("symbol") String symbol);
}
