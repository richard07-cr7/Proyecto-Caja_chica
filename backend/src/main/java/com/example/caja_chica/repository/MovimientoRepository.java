package com.example.caja_chica.repository;

import com.example.caja_chica.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @Query("SELECT m FROM Movimiento m WHERE m.cajaChica.id = :cajaId")
    List<Movimiento> findByCajaChicaId(@Param("cajaId") Long cajaId);

    @Query("SELECT m FROM Movimiento m WHERE m.cajaChica.id = :cajaId AND m.fecha BETWEEN :desde AND :hasta ORDER BY m.fecha DESC")
    List<Movimiento> findByCajaChicaIdAndFechaBetweenOrderByFechaDesc(
            @Param("cajaId") Long cajaId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
