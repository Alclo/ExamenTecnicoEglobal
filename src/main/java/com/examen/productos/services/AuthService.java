package com.examen.productos.services;

import com.examen.productos.dtos.AuthRequest;
import com.examen.productos.dtos.AuthResponse;
import com.examen.productos.entitys.Usuario;
import com.examen.productos.exceptions.ResourceNotFoundException;
import com.examen.productos.repositories.UsuarioRepository;
import com.examen.productos.services.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + request.username()));

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, "Bearer", usuario.getUsername());
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El username ya está en uso: " + request.username());
        }

        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .roles(new HashSet<>())
                .build();

        usuarioRepository.save(usuario);
        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, "Bearer", usuario.getUsername());
    }
}
