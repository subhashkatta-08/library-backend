package com.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ADMIN_ENTITY_TABLE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminEntity {
	
	@Id
	private Integer id;
	
	private String name;
	
	private String password;
	
	private String email;
	
	private Long mobileNo;
	

}
