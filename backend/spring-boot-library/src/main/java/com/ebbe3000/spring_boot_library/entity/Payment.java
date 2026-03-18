package com.ebbe3000.spring_boot_library.entity;

import com.ebbe3000.spring_boot_library.dto.PaymentDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    public PaymentDTO mapToDTO() {
        return new PaymentDTO(this.id, this.userEmail, this.amount);
    }
}
