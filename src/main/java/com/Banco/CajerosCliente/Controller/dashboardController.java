package com.Banco.CajerosCliente.Controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Banco.CajerosCliente.Service.CajeroConsultaClientService;

@Controller
@RequestMapping
public class DashboardController {

    private final CajeroConsultaClientService cajeroConsultaClientService;

    public DashboardController(CajeroConsultaClientService cajeroConsultaClientService) {
        this.cajeroConsultaClientService = cajeroConsultaClientService;
    }

    @GetMapping("dashboard")
    public String dashboard() {
        // La pantalla principal es /atms; mantenemos /dashboard como alias simple.
        return "redirect:/atms";
    }

    @GetMapping("atms")
    public String atms(HttpServletRequest request, Model model) {
        String token = readJwt(request);
        List<Map<String, Object>> atms = cajeroConsultaClientService.obtenerCajeros(token);
        model.addAttribute("atms", atms);
        return "atm";
    }

    private String readJwt(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie c : request.getCookies()) {
            if ("JWT".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
