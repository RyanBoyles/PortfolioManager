
package com.portfoliomanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfoliomanager.domain.Stock;
import com.portfoliomanager.internals.StockID;

public interface StockRepository extends JpaRepository<Stock, StockID>
{
	// Leave me empty.
}
