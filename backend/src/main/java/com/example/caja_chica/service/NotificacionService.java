package com.example.caja_chica.service;

import com.example.caja_chica.model.*;
import com.example.caja_chica.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void notificarGastoPendiente(Gasto gasto) {
        List<Usuario> admins = usuarioRepository.findAll().stream()
            .filter(u -> "ADMIN".equals(u.getRol()))
            .toList();
        for (Usuario admin : admins) {
            Notificacion n = new Notificacion();
            n.setMensaje("Nuevo gasto pendiente de aprobación: " + gasto.getDescripcion() +
                         " (S/. " + gasto.getMonto() + ") por " + gasto.getUsuario().getUsername());
            n.setTipo("PENDIENTE_APROBACION");
            n.setLeido(false);
            n.setFechaCreacion(LocalDateTime.now());
            n.setUsuario(admin);
            n.setGasto(gasto);
            notificacionRepository.save(n);
        }
    }

    @Transactional
    public void notificarAprobacion(Gasto gasto) {
        Usuario creador = gasto.getUsuario();
        Notificacion n = new Notificacion();
        n.setMensaje("Tu gasto \"" + gasto.getDescripcion() + "\" (S/. " + gasto.getMonto() + ") fue APROBADO.");
        n.setTipo("APROBADO");
        n.setLeido(false);
        n.setFechaCreacion(LocalDateTime.now());
        n.setUsuario(creador);
        n.setGasto(gasto);
        notificacionRepository.save(n);
    }

    @Transactional
    public void notificarRechazo(Gasto gasto) {
        Usuario creador = gasto.getUsuario();
        Notificacion n = new Notificacion();
        n.setMensaje("Tu gasto \"" + gasto.getDescripcion() + "\" (S/. " + gasto.getMonto() + ") fue RECHAZADO.");
        n.setTipo("RECHAZADO");
        n.setLeido(false);
        n.setFechaCreacion(LocalDateTime.now());
        n.setUsuario(creador);
        n.setGasto(gasto);
        notificacionRepository.save(n);
    }

    @Transactional
    public void notificarPresupuestoAgotado(PresupuestoArea presupuesto) {
        if (presupuesto.getDepartamento() == null) return;
        List<Usuario> usuariosDepto = usuarioRepository.findByDepartamentoNombreIgnoreCase(
            presupuesto.getDepartamento().getNombre());
        for (Usuario u : usuariosDepto) {
            Notificacion n = new Notificacion();
            BigDecimal disponible = presupuesto.getPresupuestoMensual().subtract(presupuesto.getConsumoActual());
            n.setMensaje("Presupuesto casi agotado en " + presupuesto.getDepartamento().getNombre() +
                         " para " + presupuesto.getMes() +
                         ". Disponible: S/. " + disponible);
            n.setTipo("PRESUPUESTO_AGOTADO");
            n.setLeido(false);
            n.setFechaCreacion(LocalDateTime.now());
            n.setUsuario(u);
            n.setPresupuestoArea(presupuesto);
            notificacionRepository.save(n);
        }
    }

    public List<Notificacion> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeido(usuarioId, false);
    }

    @Transactional
    public void marcarComoLeida(Long notificacionId) {
        Notificacion n = notificacionRepository.findById(notificacionId)
            .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        n.setLeido(true);
        notificacionRepository.save(n);
    }

    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId) {
        List<Notificacion> noLeidas = notificacionRepository
            .findByUsuarioIdAndLeidoOrderByFechaCreacionDesc(usuarioId, false);
        for (Notificacion n : noLeidas) {
            n.setLeido(true);
            notificacionRepository.save(n);
        }
    }
}
