package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.config.security.CustomUserDetailsService;
import com.restaurante.RestauranteAPI.config.security.JwtService;
import com.restaurante.RestauranteAPI.dto.request.LoginRequest;
import com.restaurante.RestauranteAPI.dto.request.RefreshTokenRequest;
import com.restaurante.RestauranteAPI.dto.response.LoginResponse;
import com.restaurante.RestauranteAPI.services.LoginAttemptService;
import com.restaurante.RestauranteAPI.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
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

        } catch (BadCredentialsException ex) {

            loginAttemptService.registrarFalha(ip);

            throw ex;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Token informado não é um refresh token");
        }

        String email = jwtService.extrairUsername(refreshToken);

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(email);

        if (!jwtService.tokenValido(refreshToken, userDetails)) {
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }

        String novoAccessToken =
                jwtService.gerarAccessToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponse(novoAccessToken, refreshToken)
        );
    }

}
