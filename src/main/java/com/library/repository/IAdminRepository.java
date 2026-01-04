package com.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.entity.AdminEntity;

public interface IAdminRepository extends JpaRepository<AdminEntity, Integer> {
	
	Optional<AdminEntity> findByEmail(String email);
	
	Optional<AdminEntity> findByMobileNo(Long mobileNo);

}
