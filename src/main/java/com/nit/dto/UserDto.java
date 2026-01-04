package com.nit.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class UserDto {
	
	private Integer id;
	@Nonnull
	private String name;
	@Nonnull
	private String email;
	@Nonnull
	private Long mobileNo;
	@Nonnull
	private String password;
	@Nonnull
	private Double fine;

}
