package com.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.entity.UserEntity;

public interface IUserRepository extends JpaRepository<UserEntity, Integer> {
	
	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByMobileNo(Long mobileNo);

}
