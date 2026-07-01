package com.example.caja_chica.repository;

import com.example.caja_chica.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    Optional<Usuario> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.departamento.nombre) = LOWER(:nombreDepartamento)")
    List<Usuario> findByDepartamentoNombreIgnoreCase(@Param("nombreDepartamento") String nombreDepartamento);
}
