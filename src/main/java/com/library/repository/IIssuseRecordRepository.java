package com.library.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.entity.IssueRecordEntity;

public interface IIssuseRecordRepository extends JpaRepository<IssueRecordEntity, Integer> {
	
	long countByStatus(String status);
	long countByStatusAndReturnDateIsNullAndDueDateBefore(String status, LocalDate date);
	List<IssueRecordEntity> findByStatus(String status);
	int countByUserEmailAndStatus(String userEmail, String status);
	int countByUserEmail(String userEmail);
	List<IssueRecordEntity> findByUserIdAndStatus(Integer userId, String status);
	List<IssueRecordEntity> findByUserId(Integer userId);
	boolean existsByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, String status);

}
