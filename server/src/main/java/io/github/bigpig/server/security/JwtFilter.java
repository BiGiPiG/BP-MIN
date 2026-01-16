package io.github.bigpig.server.security;

import io.github.bigpig.server.exceptions.AuthException;
import io.github.bigpig.server.exceptions.ErrorCode;
import io.github.bigpig.server.service.CustomUserDetailsService;
import io.github.bigpig.server.service.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.startsWith("/api/auth/") || path.equals("/chats")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthException(ErrorCode.AUTH_HEADER_MISSING);
            }

            String token = authHeader.substring(7);

            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;
                try {
                    userDetails = userDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException e) {
                    throw new AuthException(ErrorCode.USER_NOT_FOUND_IN_TOKEN);
                }

                if (jwtService.isValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new AuthException(ErrorCode.JWT_INVALID);
                }
            }

            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new AuthException(ErrorCode.JWT_EXPIRED);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new AuthException(ErrorCode.JWT_MALFORMED);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            throw new AuthException(ErrorCode.JWT_UNSUPPORTED);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(ErrorCode.JWT_INVALID);
        }
    }
}
