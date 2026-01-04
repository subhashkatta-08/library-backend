package com.nit.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssuseRecordDto {
	
	private Integer id;
	
	private LocalDate issueDate;
	
	private LocalDate dueDate;
	
	private LocalDate returnDate;	
	
	private LocalDate requestDate;
	
	private String status;
	
	private Integer userId;
	
	private Integer adminId;
	
	private Integer bookId;
	
	private String bookTitle;

}
