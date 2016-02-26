
package com.portfoliomanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfoliomanager.domain.Account;
import com.portfoliomanager.internals.AccountID;

public interface AccountRepository extends JpaRepository<Account, AccountID>
{
	// Leave me empty.
}
