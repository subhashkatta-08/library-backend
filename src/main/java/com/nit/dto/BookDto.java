package com.nit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
	
	private Integer id;
	
	private String title;
	
	private String author;
	
	private String category;
	
	private int totalCopies;
	
	private int availableCopies;

}
