package com.Banco.CajerosCliente.Controller;

import com.Banco.CajerosCliente.Service.AuthClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final AuthClientService authClientService;

    public LoginController(AuthClientService authClientService) {
        this.authClientService = authClientService;
    }

    @GetMapping
    public String login() {
        return "login";
    }

    @PostMapping
    public String doLogin(@RequestParam("correo") String correo,
            @RequestParam("password") String password,
            HttpServletResponse response) {

        try {
            String token = authClientService.login(correo, password);

            Cookie cookie = new Cookie("JWT", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            return "redirect:/atms";
        } catch (Exception ex) {
            return "redirect:/login?error";
        }
    }

    /**
     * Logout para pruebas y producción: - Elimina la cookie JWT (MaxAge=0). -
     * Redirige a /login.
     */
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {

        // Limpia cualquier autenticación en el contexto actual (por si acaso)
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JWT", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // Si existiera sesión (antes de stateless), también la invalidamos eliminando JSESSIONID
        Cookie jsession = new Cookie("JSESSIONID", "");
        jsession.setPath("/");
        jsession.setMaxAge(0);
        response.addCookie(jsession);

        return "redirect:/login?logout";
    }
}
