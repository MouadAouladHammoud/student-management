package com.mouad.app;

import com.mouad.app.entities.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mouad.app.repositories.PaymentRepository;
import com.mouad.app.repositories.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class StudentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentManagementApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(StudentRepository studentRepository, PaymentRepository paymentRepository) {
		return args -> {
			studentRepository.save(Student.builder().id(1).code("112233").firstName("Mohamed").programId("GLSID").email("admin@gmail.com").password("$2a$10$fMJI78OPWQRFhInCOhqlc.R1LC4s.FRLNqjNE45RlT4IWrtaAiGjm").userRole(UserRole.ADMIN).build());
			studentRepository.save(Student.builder().id(2).code("112244").firstName("Imane").programId("GLSID").email("user@gmail.com").password("$2a$10$fMJI78OPWQRFhInCOhqlc.R1LC4s.FRLNqjNE45RlT4IWrtaAiGjm").userRole(UserRole.USER).build());
			studentRepository.save(Student.builder().id(3).code("112255").firstName("Aymane").programId("BDCC").email("manager@gmail.com").password("$2a$10$fMJI78OPWQRFhInCOhqlc.R1LC4s.FRLNqjNE45RlT4IWrtaAiGjm").userRole(UserRole.MANAGER).build());
			studentRepository.save(Student.builder().id(4).code("112266").firstName("Lobna").programId("BDCC").email("lobna@gmail.com").password("123456").userRole(UserRole.USER).build());

			PaymentType[] paymentTypes = PaymentType.values();
			Random random = new Random();
			studentRepository.findAll().forEach(st -> {
				for (int i = 0; i < 5; i++) {
					int index = random.nextInt(paymentTypes.length);
					Payment payment = Payment.builder()
							.date(LocalDate.now())
							.amount(1000 + (int)(Math.random() * 20000))
							.type(paymentTypes[index])
							.status(PaymentStatus.CREATED)
							.student(st)
							.build();
					paymentRepository.save(payment);
				}
			});
		};
	}

}
