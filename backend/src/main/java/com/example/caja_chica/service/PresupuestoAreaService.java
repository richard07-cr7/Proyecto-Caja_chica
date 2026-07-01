package com.example.caja_chica.service;

import com.example.caja_chica.model.Departamento;
import com.example.caja_chica.model.PresupuestoArea;
import com.example.caja_chica.repository.DepartamentoRepository;
import com.example.caja_chica.repository.PresupuestoAreaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PresupuestoAreaService {

    @Autowired
    private PresupuestoAreaRepository presupuestoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Transactional
    public Departamento crearDepartamento(String nombre) {

        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);

        return departamentoRepository.save(departamento);
    }

    @Transactional
    public PresupuestoArea crearPresupuesto(
            Long departamentoId,
            String mes,
            BigDecimal presupuestoMensual
    ) {

        Departamento departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));

        PresupuestoArea presupuesto = new PresupuestoArea();

        presupuesto.setMes(mes);
        presupuesto.setPresupuestoMensual(presupuestoMensual);
        presupuesto.setConsumoActual(BigDecimal.ZERO);
        presupuesto.setDepartamento(departamento);

        return presupuestoRepository.save(presupuesto);
    }

    @Transactional
    public PresupuestoArea registrarConsumo(
            Long presupuestoId,
            BigDecimal monto
    ) {

        PresupuestoArea presupuesto = presupuestoRepository.findById(presupuestoId)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        BigDecimal nuevoConsumo = presupuesto.getConsumoActual().add(monto);

        if (nuevoConsumo.compareTo(presupuesto.getPresupuestoMensual()) > 0) {
            throw new RuntimeException("Se excede el presupuesto mensual");
        }

        presupuesto.setConsumoActual(nuevoConsumo);

        return presupuestoRepository.save(presupuesto);
    }

    public List<PresupuestoArea> listarPresupuestos() {
        return presupuestoRepository.findAll();
    }

    public List<Departamento> listarDepartamentos() {
        return departamentoRepository.findAll();
    }
}
