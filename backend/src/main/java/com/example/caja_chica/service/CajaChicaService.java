package com.example.caja_chica.service;

import com.example.caja_chica.model.CajaChica;
import com.example.caja_chica.repository.CajaChicaRepository;
import com.example.caja_chica.model.Departamento;
import com.example.caja_chica.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CajaChicaService {

    @Autowired
    private CajaChicaRepository repository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Transactional
    public CajaChica crearCaja(BigDecimal montoInicial, Long departamentoId) {
        if (montoInicial == null) {
            throw new RuntimeException("El monto inicial es obligatorio");
        }
        if (montoInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto inicial debe ser mayor a cero");
        }
        
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));

        CajaChica caja = new CajaChica();
        caja.setMontoInicial(montoInicial);
        caja.setSaldoActual(montoInicial);
        caja.setDepartamento(departamento);
        return repository.save(caja);
    }

    public List<CajaChica> listar() {
        return repository.findAll();
    }

    public CajaChica buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja chica no encontrada"));
    }

    @Transactional
    public CajaChica actualizarCaja(Long id, BigDecimal nuevoMonto) {
        CajaChica caja = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja chica no encontrada"));
        if (nuevoMonto == null) {
            throw new RuntimeException("El monto es obligatorio");
        }
        if (nuevoMonto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }
        caja.setMontoInicial(nuevoMonto);
        caja.setSaldoActual(nuevoMonto);
        return repository.save(caja);
    }

    @Transactional
    public void eliminarCaja(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Caja chica no encontrada");
        }
        repository.deleteById(id);
    }
}
