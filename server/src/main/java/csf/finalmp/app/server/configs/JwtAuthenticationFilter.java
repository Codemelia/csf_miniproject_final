package csf.finalmp.app.server.configs;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import csf.finalmp.app.server.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// PURPOSE OF THIS COMPONENT
// PROVIDE A FILTER FOR SPRING SECURITY AUTHENTICATION

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // single execution per request
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // get auth header from request
        String authHeader = request.getHeader("Authorization");

        // if auth header is valid
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // get string token from header
            String token = authHeader.substring(7);

            // validate token
            if (jwtUtil.validateToken(token)) {

                // get username and role
                String username = jwtUtil.getUsernameByToken(token);
                String role = jwtUtil.getRoleByToken(token);

                // set username and role to spring security user obj
                User userDetails = new User(username, "", Collections.singletonList(() -> role));

                // retrieve spring security auth token
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // set request as details for auth token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // set authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

        }

        // passes request and response along the filter chain
        filterChain.doFilter(request, response);

    }

}
