package com.example.caja_chica.controller;

import com.example.caja_chica.model.Usuario;
import com.example.caja_chica.service.UsuarioService;
import com.example.caja_chica.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Usuario request) {

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(request.getUsername());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("rol", usuario.getRol());
        response.put("id", usuario.getId().toString());
        response.put("username", usuario.getUsername());
        response.put("departamentoId",
                usuario.getDepartamento() != null ? usuario.getDepartamento().getId().toString() : "");
        response.put("departamentoNombre",
                usuario.getDepartamento() != null ? usuario.getDepartamento().getNombre() : "");
        return response;
    }
}