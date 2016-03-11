
package com.portfoliomanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.portfoliomanager.domain.Account;
import com.portfoliomanager.internals.AccountID;

public interface AccountRepository extends JpaRepository<Account, AccountID>
{
	@Query(value = "SELECT * FROM Account a WHERE a.user_email = :user_email AND a.company = :company AND a.number = :number", nativeQuery = true)
	public List<Account> findAccountByUserCompanyNumber(@Param("user_email") String user_email, @Param("company") String company, @Param("number") Long number);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO Account(user_email, label, company, number) VALUES (:user_email, :label, :company, :number)", nativeQuery = true)
	public void addAccountToUser(@Param("user_email") String userEmail, @Param("label") String label, @Param("company") String company, @Param("number") Long number);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE Account a SET a.label = :label WHERE a.company = :company AND a.number = :number", nativeQuery = true)
	public void updateAccount(@Param("company") String company, @Param("number") Long number, @Param("label") String label);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Account a WHERE a.company = :company AND a.number = :number", nativeQuery = true)
	public void removeAccount(@Param("company") String company, @Param("number") Long number);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Account a WHERE a.user_email = :user_email", nativeQuery = true)
	public void removeAccountsOwnedByUser(@Param("user_email") String userEmail);
	
	@Query(value = "SELECT shares FROM Stock_Account sa WHERE sa.accounts_company = :company AND sa.accounts_number = :number AND sa.stocks_exchange = :exchange AND sa.stocks_symbol = :symbol", nativeQuery = true)
	public Long findSharesByAccountStock(@Param("company") String company, @Param("number") Long number, @Param("exchange") String exchange, @Param("symbol") String symbol);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO Stock_Account(accounts_company, accounts_number, stocks_exchange, stocks_symbol) VALUES (:company, :number, :exchange, :symbol)", nativeQuery = true)
	public void addStockToAccount(@Param("company") String company, @Param("number") Long number, @Param("exchange") String exchange, @Param("symbol") String symbol);

	@Modifying
	@Transactional
	@Query(value = "UPDATE Stock_Account sa SET sa.shares = :shares WHERE sa.accounts_company = :company AND sa.accounts_number = :number AND sa.stocks_exchange = :exchange AND sa.stocks_symbol = :symbol", nativeQuery = true)
	public void updateSharesOfAccountStock(@Param("company") String company, @Param("number") Long number, @Param("exchange") String exchange, @Param("symbol") String symbol, @Param("shares") Long shares);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Stock_Account sa WHERE sa.accounts_company = :company AND sa.accounts_number = :number AND sa.stocks_exchange = :exchange AND sa.stocks_symbol = :symbol", nativeQuery = true)
	public void removeStockHeldInAccount(@Param("company") String company, @Param("number") Long number, @Param("exchange") String exchange, @Param("symbol") String symbol);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Stock_Account sa WHERE sa.accounts_company = :company AND sa.accounts_number = :number", nativeQuery = true)
	public void removeAllStocksHeldInAccount(@Param("company") String company, @Param("number") Long number);
}
