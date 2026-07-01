package com.example.caja_chica.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "presupuesto_area")
@Data
public class PresupuestoArea {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mes;

    private BigDecimal presupuestoMensual;

    private BigDecimal consumoActual;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
}
