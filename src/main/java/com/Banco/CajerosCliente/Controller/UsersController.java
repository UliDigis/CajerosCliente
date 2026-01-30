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

        List<Map<String, Object>> usuarios = List.of(
                Map.of("id", 1, "correo", "admin@banco.com", "rol", "ADMIN", "estado", "ACTIVO"),
                Map.of("id", 2, "correo", "cliente@banco.com", "rol", "CLIENTE", "estado", "ACTIVO"),
                Map.of("id", 3, "correo", "soporte@banco.com", "rol", "ADMIN", "estado", "INACTIVO")
        );

        model.addAttribute("usuarios", usuarios);
        return "users";
    }
}
