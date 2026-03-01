package auth_service.filter;

import auth_service.entity.User;
import auth_service.exception.AuthenticationErrorHandler;
import auth_service.exception.InvalidAccessTokenException;
import auth_service.service.CustomUserDetailsService;
import auth_service.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationErrorHandler errorHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = (User) userDetailsService.loadUserByUsername(username);

                if (!jwtService.isValid(jwt, user)) {
                    log.warn("Token validation failed for user: {}", username);
                    errorHandler.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "Invalid access token");
                    return;
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Authenticated user: {}", username);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired for user: {}", e.getClaims().getSubject());
            errorHandler.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Access token has expired");

        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token");
            errorHandler.sendErrorResponse(response, HttpStatus.BAD_REQUEST, "TOKEN_MALFORMED", "Invalid token format");

        } catch (InvalidAccessTokenException e) {
            log.warn("Invalid access token: {}", e.getMessage());
            errorHandler.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication", e);
            errorHandler.sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Authentication service error");
        }
    }
}
