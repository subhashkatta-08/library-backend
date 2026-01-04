package com.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "BOOK_ENTITY_TABLE")
public class BookEntity {
	
	@Id
	@SequenceGenerator(name = "seq1", sequenceName = "USER_ENTITY_SEQ", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seq1")
	private Integer id;
	
	private String title;
	
	private String author;
	
	private String category;
	
	private int totalCopies;
	
	private int availableCopies;

}
