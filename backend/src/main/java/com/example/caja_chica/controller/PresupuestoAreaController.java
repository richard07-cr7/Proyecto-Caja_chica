package com.example.caja_chica.controller;

import com.example.caja_chica.model.Departamento;
import com.example.caja_chica.model.PresupuestoArea;
import com.example.caja_chica.service.PresupuestoAreaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/presupuesto")
public class PresupuestoAreaController {

    @Autowired
    private PresupuestoAreaService service;

    @PostMapping("/departamento")
    public Departamento crearDepartamento(
            @RequestParam String nombre
    ) {
        return service.crearDepartamento(nombre);
    }

    @PostMapping
    public PresupuestoArea crearPresupuesto(
            @RequestParam Long departamentoId,
            @RequestParam String mes,
            @RequestParam BigDecimal presupuestoMensual
    ) {
        return service.crearPresupuesto(
                departamentoId,
                mes,
                presupuestoMensual
        );
    }

    @PostMapping("/consumo")
    public PresupuestoArea registrarConsumo(
            @RequestParam Long presupuestoId,
            @RequestParam BigDecimal monto
    ) {
        return service.registrarConsumo(
                presupuestoId,
                monto
        );
    }

    @GetMapping
    public List<PresupuestoArea> listarPresupuestos() {
        return service.listarPresupuestos();
    }

    @GetMapping("/departamentos")
    public List<Departamento> listarDepartamentos() {
        return service.listarDepartamentos();
    }
}