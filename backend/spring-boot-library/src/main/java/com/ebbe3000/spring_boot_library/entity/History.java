package com.ebbe3000.spring_boot_library.entity;

import com.ebbe3000.spring_boot_library.dto.BookDTO;
import com.ebbe3000.spring_boot_library.dto.HistoryDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "history")
@NoArgsConstructor
@Data
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "checkout_date")
    private String checkoutDate;

    @Column(name = "returned_date")
    private String returnedDate;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "description")
    private String description;

    @Column(name = "img")
    private String img;

    public History(String userEmail, String checkoutDate, String returnedDate, String title,
                   String author, String description, String img) {
        this.userEmail = userEmail;
        this.checkoutDate = checkoutDate;
        this.returnedDate = returnedDate;
        this.title = title;
        this.author = author;
        this.description = description;
        this.img = img;
    }

    public HistoryDTO mapToDTO() {
        return new HistoryDTO(
                this.id,
                this.userEmail,
                this.checkoutDate,
                this.returnedDate,
                this.title,
                this.author,
                this.description,
                this.img);
    }
}
