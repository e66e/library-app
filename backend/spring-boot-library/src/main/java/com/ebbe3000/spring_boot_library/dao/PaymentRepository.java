package com.ebbe3000.spring_boot_library.dao;

import com.ebbe3000.spring_boot_library.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByUserEmail(String userEmail);
}
