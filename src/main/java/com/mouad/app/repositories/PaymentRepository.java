package com.mouad.app.repositories;

import com.mouad.app.entities.Payment;
import com.mouad.app.entities.PaymentStatus;
import com.mouad.app.entities.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudentCode(String code);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByType(PaymentType type);
}
