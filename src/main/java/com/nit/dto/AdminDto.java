package com.nit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto {
	
	private Integer id;
	
	private String name;
	
	private String password;
	
	private String email;
	
	private Long mobileNo;

}
