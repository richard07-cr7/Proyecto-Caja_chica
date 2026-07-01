package com.example.caja_chica.controller;

import com.example.caja_chica.model.Gasto;
import com.example.caja_chica.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    private final String CARPETA_COMPROBANTES = "comprobantes/";

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    @PostMapping
    public Gasto registrar(@RequestBody Gasto gasto,
                           @RequestParam Long cajaId,
                           @RequestParam Long presupuestoId,
                           Authentication authentication) {
        String username = authentication.getName();
        return gastoService.registrarGasto(gasto, cajaId, presupuestoId, username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/aprobar")
    public Gasto aprobar(@PathVariable Long id) {
        return gastoService.aprobarGasto(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/rechazar")
    public Gasto rechazar(@PathVariable Long id) {
        return gastoService.rechazarGasto(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Gasto> listarTodos() {
        return gastoService.listarTodos();
    }

    @PreAuthorize("hasRole('EMPLEADO')")
    @GetMapping("/mis-gastos/{usuarioId}")
    public List<Gasto> listarPorUsuario(@PathVariable Long usuarioId) {
        return gastoService.listarPorUsuario(usuarioId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/estado")
    public List<Gasto> listarPorEstado(@RequestParam String valor) {
        return gastoService.listarPorEstado(valor);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    @GetMapping("/categoria")
    public List<Gasto> listarPorCategoria(@RequestParam String valor) {
        return gastoService.listarPorCategoria(valor);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    @PostMapping("/{id}/comprobante")
    public Gasto subirComprobante(@PathVariable Long id,
                                  @RequestParam MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        String tipoArchivo = archivo.getContentType();
        if (tipoArchivo == null ||
            (!tipoArchivo.equals("image/jpeg") &&
             !tipoArchivo.equals("image/png") &&
             !tipoArchivo.equals("application/pdf"))) {
            throw new RuntimeException("Solo se permiten archivos JPG, PNG o PDF");
        }

        File carpeta = new File(CARPETA_COMPROBANTES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        Path ruta = Paths.get(CARPETA_COMPROBANTES + nombreArchivo);
        Files.write(ruta, archivo.getBytes());

        return gastoService.agregarComprobante(id, ruta.toString());
    }
}
