package com.example.caja_chica.service;

import com.example.caja_chica.model.CajaChica;
import com.example.caja_chica.model.Movimiento;
import com.example.caja_chica.repository.CajaChicaRepository;
import com.example.caja_chica.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CajaChicaRepository cajaChicaRepository;

    @Transactional
    public Movimiento registrarMovimiento(Long cajaId, String tipo, BigDecimal monto, String descripcion) {
        CajaChica caja = cajaChicaRepository.findById(cajaId)
                .orElseThrow(() -> new RuntimeException("Caja chica no encontrada"));

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a 0");
        }

        if (tipo.equalsIgnoreCase("INGRESO")) {
            caja.setSaldoActual(caja.getSaldoActual().add(monto));
        } else if (tipo.equalsIgnoreCase("EGRESO")) {
            if (caja.getSaldoActual().compareTo(monto) < 0) {
                throw new RuntimeException("Saldo insuficiente");
            }
            caja.setSaldoActual(caja.getSaldoActual().subtract(monto));
        } else {
            throw new RuntimeException("Tipo inválido");
        }

        cajaChicaRepository.save(caja);

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(tipo.toUpperCase());
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setCajaChica(caja);

        return movimientoRepository.save(movimiento);
    }

    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAll();
    }

    public List<Movimiento> listarPorCaja(Long cajaId) {
        return movimientoRepository.findByCajaChicaId(cajaId);
    }
}