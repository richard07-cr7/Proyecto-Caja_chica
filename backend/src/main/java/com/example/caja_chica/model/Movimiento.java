package com.example.caja_chica.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento")
@Data
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    private BigDecimal monto;

    private String descripcion;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "caja_id")
    private CajaChica cajaChica;
}