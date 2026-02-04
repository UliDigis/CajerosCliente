package com.Banco.CajerosCliente.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro para asegurar que los archivos estáticos tengan el Content-Type correcto
 */
@Component
public class ContentTypeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        // Establecer Content-Type correcto para archivos estáticos
        if (path.endsWith(".js")) {
            response.setContentType("application/javascript;charset=UTF-8");
        } else if (path.endsWith(".css")) {
            response.setContentType("text/css;charset=UTF-8");
        } else if (path.endsWith(".html")) {
            response.setContentType("text/html;charset=UTF-8");
        } else if (path.endsWith(".json")) {
            response.setContentType("application/json;charset=UTF-8");
        } else if (path.endsWith(".png")) {
            response.setContentType("image/png");
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            response.setContentType("image/jpeg");
        } else if (path.endsWith(".gif")) {
            response.setContentType("image/gif");
        } else if (path.endsWith(".svg")) {
            response.setContentType("image/svg+xml");
        } else if (path.endsWith(".ico")) {
            response.setContentType("image/x-icon");
        } else if (path.endsWith(".woff")) {
            response.setContentType("font/woff");
        } else if (path.endsWith(".woff2")) {
            response.setContentType("font/woff2");
        } else if (path.endsWith(".ttf")) {
            response.setContentType("font/ttf");
        } else if (path.endsWith(".eot")) {
            response.setContentType("application/vnd.ms-fontobject");
        }
        
        filterChain.doFilter(request, response);
    }
}
