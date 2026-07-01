package com.example.caja_chica.repository;

import com.example.caja_chica.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    @Query("SELECT d FROM Departamento d WHERE LOWER(d.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Departamento> buscarPorNombreContiene(@Param("texto") String texto);

    @Query("SELECT d FROM Departamento d WHERE EXISTS (SELECT c FROM CajaChica c WHERE c.departamento = d)")
    List<Departamento> findWithCajaChica();
}
