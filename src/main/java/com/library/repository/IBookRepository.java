package com.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.entity.BookEntity;

public interface IBookRepository extends JpaRepository<BookEntity, Integer> {

}
