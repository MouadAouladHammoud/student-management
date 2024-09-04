package com.mouad.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private double amount;
    private PaymentType type;
    private PaymentStatus status;
    private String file;
    @ManyToOne
    // @JsonIgnore // Remarque : Ceci juste ajouté pour afficher uniquement les permissions de l'utilisateur connecté actuellement, et non celles des autres étudiants récupérés par d'autres fonctions.
    private Student student;
}
