
package com.portfoliomanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.portfoliomanager.domain.User;
import com.portfoliomanager.internals.UserID;

public interface UserRepository extends JpaRepository<User, UserID>
{
	@Query(value = "SELECT * FROM User u WHERE u.email = :email AND u.password = :password", nativeQuery = true)
	public List<User> findUserByLogin(@Param("email") String email, @Param("password") String password);
	
	@Query(value = "SELECT * FROM User u WHERE u.email = :email", nativeQuery = true)
	public List<User> findUserByEmail(@Param("email") String email);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO User(email, name, password) VALUES (:email, :name, :password)", nativeQuery = true)
	public void addUser(@Param("email") String email, @Param("name") String name, @Param("password") String password);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE User u SET name = :name, password = :password WHERE u.email = :email", nativeQuery = true)
	public void updateUser(@Param("email") String email, @Param("name") String name, @Param("password") String password);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM User u WHERE u.email = :email", nativeQuery = true)
	public void removeUser(@Param("email") String email);
}
