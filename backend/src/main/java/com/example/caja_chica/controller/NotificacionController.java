package com.example.caja_chica.controller;

import com.example.caja_chica.model.Notificacion;
import com.example.caja_chica.model.Usuario;
import com.example.caja_chica.repository.UsuarioRepository;
import com.example.caja_chica.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-notificaciones")
    public List<Notificacion> misNotificaciones(Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        return notificacionService.listarPorUsuario(usuario.getId());
    }

    @GetMapping("/no-leidas")
    public Map<String, Long> noLeidas(Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        long count = notificacionService.contarNoLeidas(usuario.getId());
        return Map.of("count", count);
    }

    @PutMapping("/{id}/leer")
    public void marcarLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
    }

    @PutMapping("/leer-todas")
    public void marcarTodasLeidas(Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        notificacionService.marcarTodasComoLeidas(usuario.getId());
    }
}
