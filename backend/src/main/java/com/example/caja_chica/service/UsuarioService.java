package com.example.caja_chica.service;

import com.example.caja_chica.model.Usuario;
import com.example.caja_chica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername());
        if (existente.isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            throw new RuntimeException("El rol es obligatorio");
        }

        usuario.setRol(usuario.getRol().toUpperCase());

        if (!usuario.getRol().equals("ADMIN") && !usuario.getRol().equals("EMPLEADO")) {
            throw new RuntimeException("El rol debe ser ADMIN o EMPLEADO");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario datosNuevos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (datosNuevos.getUsername() != null && !datosNuevos.getUsername().isBlank()) {
            usuario.setUsername(datosNuevos.getUsername());
        }

        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datosNuevos.getPassword()));
        }

        if (datosNuevos.getRol() != null && !datosNuevos.getRol().isBlank()) {
            String nuevoRol = datosNuevos.getRol().toUpperCase();
            if (!nuevoRol.equals("ADMIN") && !nuevoRol.equals("EMPLEADO")) {
                throw new RuntimeException("El rol debe ser ADMIN o EMPLEADO");
            }
            usuario.setRol(nuevoRol);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}