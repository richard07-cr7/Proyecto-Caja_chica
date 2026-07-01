package com.example.caja_chica.repository;

import com.example.caja_chica.model.CajaChica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CajaChicaRepository extends JpaRepository<CajaChica, Long> {

    @Query("SELECT c FROM CajaChica c WHERE c.saldoActual >= :saldoMinimo AND LOWER(c.departamento.nombre) = LOWER(:nombreDepartamento)")
    List<CajaChica> findBySaldoActualGreaterThanEqualAndDepartamentoNombreIgnoreCase(
            @Param("saldoMinimo") BigDecimal saldoMinimo,
            @Param("nombreDepartamento") String nombreDepartamento);
}
