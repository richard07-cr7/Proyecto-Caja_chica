package com.example.caja_chica.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "caja_chica")
@Data
public class CajaChica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El monto inicial es obligatorio")
    @Positive(message = "El monto inicial debe ser mayor a cero")
    private BigDecimal montoInicial;

    private BigDecimal saldoActual;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
}
