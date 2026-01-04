package com.library.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;



@Entity
@Table(name = "USER_ENTITY_TABLE")
@Data
public class UserEntity {
	
	@Id
	@SequenceGenerator(name = "seq1", sequenceName = "USER_ENTITY_SEQ", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seq1")
	private Integer id;
	
	private String name;
	
	private String password;
	
	private String email;
	
	private Long mobileNo;
	
	private Double fine = 0.0;
	
	@OneToMany(mappedBy = "user")
	private List<IssueRecordEntity> records;

}
