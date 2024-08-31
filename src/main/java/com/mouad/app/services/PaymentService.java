package com.mouad.app.services;

import com.mouad.app.dto.NewPaymentDto;
import com.mouad.app.entities.Payment;
import com.mouad.app.entities.PaymentStatus;
import com.mouad.app.entities.PaymentType;
import com.mouad.app.entities.Student;
import com.mouad.app.repositories.PaymentRepository;
import com.mouad.app.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    private PaymentRepository paymentRepository;
    private StudentRepository studentRepository;

    public PaymentService(PaymentRepository paymentRepository, StudentRepository studentRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    // public Payment savePayment(MultipartFile file, double amount, PaymentType type, LocalDate date, String studentCode) throws IOException {
    public Payment savePayment(MultipartFile file, NewPaymentDto newPaymentDto) throws IOException {
        Path folderPath = Paths.get(System.getProperty("user.home"),"enset-students","payments");
        if(!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }
        String fileName = UUID.randomUUID().toString();
        Path filePath = Paths.get(System.getProperty("user.home"),"enset-students","payments",fileName+".pdf");
        Files.copy(file.getInputStream(), filePath);
        // Student student = studentRepository.findByCode(studentCode);
        Student student = studentRepository.findByCode(newPaymentDto.getStudentCode());
        Payment payment = Payment.builder()
                //.type(type)
                .type(newPaymentDto.getType())
                .status(PaymentStatus.CREATED)
                //.date(date)
                .date(newPaymentDto.getDate())
                .student(student)
                //.amount(amount)
                .amount(newPaymentDto.getAmount())
                .file(filePath.toUri().toString())
                .build();
        return paymentRepository.save(payment);
    }

    public byte[] getPaymentFile(Long id) throws IOException {
        Payment payment = paymentRepository.findById(id).get();
        return Files.readAllBytes(Path.of(URI.create(payment.getFile())));

    }

    public Payment updatePaymentStatus(PaymentStatus status, Long paymentId){
        Payment payment = paymentRepository.findById(paymentId).get();
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }
}
