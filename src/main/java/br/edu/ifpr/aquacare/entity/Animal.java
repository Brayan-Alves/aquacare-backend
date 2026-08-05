package br.edu.ifpr.aquacare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Animal")
@Getter
@Setter
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
