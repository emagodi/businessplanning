package zw.co.zetdc.businessplanning.config;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import zw.co.zetdc.businessplanning.service.JwtService;


import java.io.IOException;

/**
    * Our jwt class extends OnePerRequestFilter to be executed on every http request
    * We can also implement the Filter interface (jakarta EE), but Spring gives us a OncePerRequestFilter
        class that extends the GenericFilterBean, which also implements the Filter interface.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Allow access to Swagger UI and related endpoints without authentication
        if (request.getRequestURI().contains("/v2/api-docs") ||
                request.getRequestURI().contains("/swagger-resources") ||
                request.getRequestURI().contains("/swagger-ui") ||
                request.getRequestURI().contains("/webjars") ||
                request.getRequestURI().contains("/swagger-ui.html")||
                request.getRequestURI().contains("/swagger-ui.html")){
            filterChain.doFilter(request, response);
            return;
        }

        // Try to get JWT from cookie or Authorization header
        String jwt = jwtService.getJwtFromCookies(request);
        final String authHeader = request.getHeader("Authorization");

        if ((jwt == null && (authHeader == null || !authHeader.startsWith("Bearer "))) || request.getRequestURI().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // If the JWT is not in the cookies but in the "Authorization" header
        if (jwt == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Remove "Bearer "
        }

        final String userEmail = jwtService.extractUserName(jwt);

        // If userEmail is valid and there's no existing authentication
        if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Update the Spring Security context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException e) {
                System.err.println("User not found: " + userEmail); // Log the error
            }
        }

        filterChain.doFilter(request, response);
    }
}
