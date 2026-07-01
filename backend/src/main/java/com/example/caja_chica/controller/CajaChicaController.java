package com.example.caja_chica.controller;

import com.example.caja_chica.model.CajaChica;
import com.example.caja_chica.service.CajaChicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/caja")
public class CajaChicaController {

    @Autowired
    private CajaChicaService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CajaChica crear(@RequestParam BigDecimal montoInicial, @RequestParam Long departamentoId) {
        return service.crearCaja(montoInicial, departamentoId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    @GetMapping
    public List<CajaChica> listar() {
        return service.listar();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    @GetMapping("/{id}")
    public CajaChica buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CajaChica actualizar(@PathVariable Long id, @RequestParam BigDecimal nuevoMonto) {
        return service.actualizarCaja(id, nuevoMonto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminarCaja(id);
    }
}