package com.example.caja_chica.repository;

import com.example.caja_chica.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByUsuarioId(Long usuarioId);
    List<Gasto> findByEstado(String estado);
    List<Gasto> findByCajaChicaId(Long cajaId);
    List<Gasto> findByCategoria(String categoria);

    @Query("SELECT g FROM Gasto g WHERE g.estado = :estado AND g.cajaChica.id = :cajaId")
    List<Gasto> findByEstadoAndCaja(
        @Param("estado") String estado,
        @Param("cajaId") Long cajaId
    );
}