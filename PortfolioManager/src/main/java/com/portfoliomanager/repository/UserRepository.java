
package com.portfoliomanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfoliomanager.domain.User;
import com.portfoliomanager.internals.UserID;

public interface UserRepository extends JpaRepository<User, UserID>
{
	// Leave me empty.
}
