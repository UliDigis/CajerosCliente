package com.Banco.CajerosCliente.Controller;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersController {

    /**
     * CLIENTE: pantalla de usuarios solo para ADMIN. Por ahora retorna datos
     * mock. Luego se conecta al SERVICE.
     */
    @GetMapping("/users")
    public String users(Model model) {
        // Se carga dinámicamente por AJAX desde /api/usuarios.
        model.addAttribute("users", List.<Map<String, Object>>of());
        return "users";
    }
}
