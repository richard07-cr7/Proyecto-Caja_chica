package com.example.caja_chica;

import com.example.caja_chica.model.Usuario;
import com.example.caja_chica.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CajaChicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CajaChicaApplication.class, args);
    }

    @Bean
    CommandLineRunner crearAdministradorInicial(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder) {

        return args -> {

            if (usuarioRepository.findByUsername("admin").isEmpty()) {

                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setDepartamento(null);

                usuarioRepository.save(admin);

                System.out.println("Usuario administrador creado correctamente.");
            }
        };
    }
}