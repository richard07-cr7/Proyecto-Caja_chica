package com.example.caja_chica.controller;

import com.example.caja_chica.model.Movimiento;
import com.example.caja_chica.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @PostMapping
    public Movimiento registrarMovimiento(
            @RequestParam Long cajaId,
            @RequestParam String tipo,
            @RequestParam BigDecimal monto,
            @RequestParam String descripcion
    ) {
        return movimientoService.registrarMovimiento(cajaId, tipo, monto, descripcion);
    }

    @GetMapping
    public List<Movimiento> listarTodos() {
        return movimientoService.listarTodos();
    }

    @GetMapping("/caja/{cajaId}")
    public List<Movimiento> listarPorCaja(@PathVariable Long cajaId) {
        return movimientoService.listarPorCaja(cajaId);
    }
}