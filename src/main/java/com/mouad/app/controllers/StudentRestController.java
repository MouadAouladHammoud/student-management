package com.mouad.app.controllers;

import com.mouad.app.dto.NewPaymentDto;
import com.mouad.app.entities.Payment;
import com.mouad.app.entities.PaymentStatus;
import com.mouad.app.entities.Student;
import com.mouad.app.repositories.PaymentRepository;
import com.mouad.app.repositories.StudentRepository;
import com.mouad.app.services.PaymentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
public class StudentRestController {
    private StudentRepository studentRepository;
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    public StudentRestController(StudentRepository studentRepository, PaymentRepository paymentRepository, PaymentService paymentService) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    @GetMapping(path = "/students")
    public List<Student> allStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/students/{code}")
    public Student getStudentByCode(@PathVariable String code) {
        return studentRepository.findByCode(code);
    }

    @GetMapping(path = "/studentsByProgram")
    public List<Student> studentsByProgram(@RequestParam String programId) {
        return studentRepository.findByProgramId(programId);
    }

    @GetMapping("/payments")
    public List<Payment> allPayments() {
        return paymentRepository.findAll();
    }

    @GetMapping("/payments/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentRepository.findById(id).get();
    }

    @GetMapping("/students/{code}/payments")
    public List<Payment> paymentsByStudentCode(@PathVariable String code) {
        return paymentRepository.findByStudentCode(code);
    }

    @GetMapping("/paymentsByStatus")
    public List<Payment> paymentsByStaus(@RequestParam PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @PutMapping("/payments/{paymentId}/updateStatus")
    public Payment updatePaymentStatus(@RequestParam PaymentStatus status, @PathVariable Long paymentId) {
        return paymentService.updatePaymentStatus(status,paymentId);
    }

    // @PostMapping(path="/payments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public Payment savePayment(@RequestParam MultipartFile file, double amount, PaymentType type, LocalDate date, String studentCode) throws IOException {
        // return paymentService.savePayment(file,amount,type,date,studentCode);
    @PostMapping(path="/payments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Payment savePayment(@RequestParam("file") MultipartFile file, NewPaymentDto newPaymentDto) throws IOException {
        return paymentService.savePayment(file,newPaymentDto);
    }

    @GetMapping(path="payments/{id}/file",produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getPaymentFile(@PathVariable Long id) throws IOException {
        return paymentService.getPaymentFile(id);
    }
}
