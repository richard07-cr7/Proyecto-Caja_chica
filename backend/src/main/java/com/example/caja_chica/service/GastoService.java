package com.example.caja_chica.service;

import com.example.caja_chica.model.CajaChica;
import com.example.caja_chica.model.Gasto;
import com.example.caja_chica.model.Usuario;
import com.example.caja_chica.repository.CajaChicaRepository;
import com.example.caja_chica.repository.GastoRepository;
import com.example.caja_chica.repository.UsuarioRepository;
import com.example.caja_chica.model.PresupuestoArea;
import com.example.caja_chica.repository.PresupuestoAreaRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private CajaChicaRepository cajaChicaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PresupuestoAreaRepository presupuestoAreaRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Transactional
    public Gasto registrarGasto(Gasto gasto, Long cajaId, Long presupuestoId, String username) {
        CajaChica caja = cajaChicaRepository.findById(cajaId)
                .orElseThrow(() -> new RuntimeException("Caja chica no encontrada"));

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PresupuestoArea presupuesto = presupuestoAreaRepository.findById(presupuestoId)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        gasto.setEstado("PENDIENTE");
        gasto.setFecha(LocalDateTime.now());
        gasto.setCajaChica(caja);
        gasto.setUsuario(usuario);
        gasto.setPresupuestoArea(presupuesto);

        Gasto guardado = gastoRepository.save(gasto);
        notificacionService.notificarGastoPendiente(guardado);
        return guardado;
    }

    @Transactional
    public Gasto aprobarGasto(Long gastoId) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        if (!gasto.getEstado().equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden aprobar gastos en estado PENDIENTE");
        }

        CajaChica caja = gasto.getCajaChica();
        PresupuestoArea presupuesto = gasto.getPresupuestoArea();
        if (caja.getSaldoActual().compareTo(gasto.getMonto()) < 0) {
            throw new RuntimeException("Saldo insuficiente en la caja chica");
        }
        
        BigDecimal nuevoConsumo = presupuesto.getConsumoActual().add(gasto.getMonto());
        if (nuevoConsumo.compareTo(presupuesto.getPresupuestoMensual()) > 0) {
            throw new RuntimeException("Se excede el presupuesto mensual");
        }

        caja.setSaldoActual(caja.getSaldoActual().subtract(gasto.getMonto()));
        cajaChicaRepository.save(caja);
        presupuesto.setConsumoActual(nuevoConsumo);
        presupuestoAreaRepository.save(presupuesto);

        gasto.setEstado("APROBADO");
        Gasto aprobado = gastoRepository.save(gasto);
        notificacionService.notificarAprobacion(aprobado);

        BigDecimal limitePresupuesto = presupuesto.getPresupuestoMensual()
            .multiply(new BigDecimal("0.80"));
        if (presupuesto.getConsumoActual().compareTo(limitePresupuesto) >= 0) {
            notificacionService.notificarPresupuestoAlerta(presupuesto);
        }

        BigDecimal diezPorCiento = caja.getMontoInicial()
            .multiply(new BigDecimal("0.10"));
        if (caja.getSaldoActual().compareTo(diezPorCiento) <= 0) {
            notificacionService.notificarSaldoBajo(caja);
        }

        return aprobado;
    }

    @Transactional
    public Gasto rechazarGasto(Long gastoId) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        if (!gasto.getEstado().equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden rechazar gastos en estado PENDIENTE");
        }

        gasto.setEstado("RECHAZADO");
        Gasto rechazado = gastoRepository.save(gasto);
        notificacionService.notificarRechazo(rechazado);
        return rechazado;
    }

    public List<Gasto> listarTodos() {
        return gastoRepository.findAll();
    }

    public List<Gasto> listarPorUsuario(Long usuarioId) {
        return gastoRepository.findByUsuarioId(usuarioId);
    }

    public List<Gasto> listarPorEstado(String estado) {
        return gastoRepository.findByEstado(estado.toUpperCase());
    }

    public List<Gasto> listarPorCategoria(String categoria) {
        return gastoRepository.findByCategoria(categoria.toUpperCase());
    }

    @Transactional
    public Gasto agregarComprobante(Long gastoId, String rutaComprobante) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        gasto.setComprobante(rutaComprobante);
        return gastoRepository.save(gasto);
    }
}
