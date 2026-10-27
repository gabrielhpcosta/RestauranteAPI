package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.config.security.CustomUserDetailsService;
import com.restaurante.RestauranteAPI.config.security.JwtService;
import com.restaurante.RestauranteAPI.dto.request.LoginRequest;
import com.restaurante.RestauranteAPI.dto.request.RefreshTokenRequest;
import com.restaurante.RestauranteAPI.dto.response.LoginResponse;
import com.restaurante.RestauranteAPI.services.LoginAttemptService;
import com.restaurante.RestauranteAPI.services.UsuarioService;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@SecurityRequirements()
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final CustomUserDetailsService customUserDetailsService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UsuarioService usuarioService, CustomUserDetailsService customUserDetailsService, LoginAttemptService loginAttemptService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.customUserDetailsService = customUserDetailsService;
        this.loginAttemptService = loginAttemptService;
    }



    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ip = httpRequest.getRemoteAddr();

        loginAttemptService.verificarBloqueio(ip);

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getSenha()
                            )
                    );

            loginAttemptService.registrarSucesso(ip);

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String accessToken =
                    jwtService.gerarAccessToken(userDetails);

            String refreshToken =
                    jwtService.gerarRefreshToken(userDetails);

            return ResponseEntity.ok(
                    new LoginResponse(accessToken, refreshToken)
            );

        } catch (BadCredentialsException | DisabledException ex) {

            loginAttemptService.registrarFalha(ip);

            // mesma resposta para senha errada e usuário desativado,
            // para não revelar quais emails existem no sistema
            throw new BadCredentialsException("Email ou senha inválidos");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Informe o refresh token");
        }

        UserDetails userDetails;

        try {
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("Token informado não é um refresh token");
            }

            String email = jwtService.extrairUsername(refreshToken);
            userDetails = customUserDetailsService.loadUserByUsername(email);

        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // token mal formado, assinatura errada, expirado ou de usuário inexistente
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }

        if (!jwtService.tokenValido(refreshToken, userDetails)) {
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }

        if (!userDetails.isEnabled()) {
            throw new BadCredentialsException("Usuário desativado");
        }

        String novoAccessToken =
                jwtService.gerarAccessToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponse(novoAccessToken, refreshToken)
        );
    }

}
