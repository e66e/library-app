package com.ebbe3000.spring_boot_library.entity;

import com.ebbe3000.spring_boot_library.dto.BookDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "description")
    private String description;

    @Column(name = "copies")
    private int copies;

    @Column(name = "copies_available")
    private int copiesAvailable;

    @Column(name = "category")
    private String category;

    @Column(name = "img")
    private String img;

    public BookDTO mapToDTO() {
        return new BookDTO(this.id,
                           this.title,
                           this.author,
                           this.description,
                           this.copies,
                           this.copiesAvailable,
                           this.category,
                           this.img);
    }
}
