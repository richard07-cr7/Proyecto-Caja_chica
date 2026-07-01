package com.example.caja_chica.repository;

import com.example.caja_chica.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidoOrderByFechaCreacionDesc(Long usuarioId, Boolean leido);

    long countByUsuarioIdAndLeido(Long usuarioId, Boolean leido);
}
