package com.nocountry.fintech.security;

import com.nocountry.fintech.service.UsuarioService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected  void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @Nonnull FilterChain filterChain
            ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        //si no hay cabecera Authorization o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Extraer el token (quitando "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 3. Extraer el email/username del token
            userEmail = jwtService.extractUsername(jwt); // Asegúrate de ajustar el nombre del método según tu JwtService

            // 4. Si hay email y el usuario no está autenticado aún en el contexto
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 5. Validar que el token sea válido para ese usuario
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. ¡LÍNEA CLAVE! Establecer la autenticación en Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("No se pudo autenticar el usuario con el token JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
