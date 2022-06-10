package com.ms.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.user.model.bean.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>
{
	Optional<User> findByEmail(String email);
	Optional<List<User>> findByName(String name);
}
