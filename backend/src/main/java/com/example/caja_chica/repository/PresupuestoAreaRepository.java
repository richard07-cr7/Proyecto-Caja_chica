package com.example.caja_chica.repository;

import com.example.caja_chica.model.PresupuestoArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PresupuestoAreaRepository extends JpaRepository<PresupuestoArea, Long> {

    @Query("SELECT p FROM PresupuestoArea p WHERE p.departamento.id = :deptoId AND p.mes = :mes")
    Optional<PresupuestoArea> findByDepartamentoAndMes(
        @Param("deptoId") Long deptoId,
        @Param("mes") String mes
    );

    @Query("SELECT p FROM PresupuestoArea p WHERE p.departamento.id = :deptoId")
    List<PresupuestoArea> findByDepartamento(@Param("deptoId") Long deptoId);
}