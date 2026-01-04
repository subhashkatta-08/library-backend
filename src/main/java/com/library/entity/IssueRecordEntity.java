package com.library.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "ISSUSE_RECORD_ENTTITY")
@Data
public class IssueRecordEntity {
	
	@Id
	@SequenceGenerator(name = "seq1", sequenceName = "USER_ENTITY_SEQ", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seq1")
	private Integer id;
	
	private LocalDate issueDate;
	
	private LocalDate dueDate;
	
	private LocalDate returnDate;	
	
	private LocalDate requestDate;
	
	private String status;
	
	@ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "ID")
	private UserEntity user;
	
	@ManyToOne(targetEntity = AdminEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "issued_by_admin_id", referencedColumnName = "ID", nullable = true)
	private AdminEntity issuedBy;
	
	@ManyToOne(targetEntity = BookEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", referencedColumnName = "ID")
	private BookEntity book;

}
