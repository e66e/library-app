package com.ebbe3000.spring_boot_library.dao;

import com.ebbe3000.spring_boot_library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.title ILIKE CONCAT('%', :title, '%')")
    Page<Book> findByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.category ILIKE :category")
    Page<Book> findByCategory(@Param("category") String category, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.id IN :book_ids")
    List<Book>findBooksByBookIds(@Param("book_ids") List<Long> bookId);
}
