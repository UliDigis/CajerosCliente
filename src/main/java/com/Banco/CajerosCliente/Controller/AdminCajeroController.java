package com.Banco.CajerosCliente.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Banco.CajerosCliente.Service.CajeroAdminClientService;

@Controller
@RequestMapping("/admin/cajeros")
public class AdminCajeroController {

    private final CajeroAdminClientService cajeroAdminClientService;

    public AdminCajeroController(CajeroAdminClientService cajeroAdminClientService) {
        this.cajeroAdminClientService = cajeroAdminClientService;
    }

    /**
     * CLIENTE: dispara recarga de un cajero (SERVICE: /admin/cajeros/{codigo}/recargar).
     */
    @PostMapping("/{codigo}/recargar")
    public String recargarUno(@PathVariable("codigo") String codigo,
                              HttpServletRequest request,
                              RedirectAttributes ra) {
        try {
            String token = readJwt(request);
            cajeroAdminClientService.recargarCajero(codigo, token);
            ra.addAttribute("ok", "Cajero recargado: " + codigo);
        } catch (Exception ex) {
            ra.addAttribute("err", "No se pudo recargar el cajero: " + codigo);
        }
        return "redirect:/atms";
    }

    /**
     * CLIENTE: dispara recarga masiva (SERVICE: /admin/cajeros/recargar-todos).
     */
    @PostMapping("/recargar-todos")
    public String recargarTodos(HttpServletRequest request, RedirectAttributes ra) {
        try {
            String token = readJwt(request);
            cajeroAdminClientService.recargarTodos(token);
            ra.addAttribute("ok", "Recarga masiva ejecutada");
        } catch (Exception ex) {
            ra.addAttribute("err", "No se pudo ejecutar la recarga masiva");
        }
        return "redirect:/atms";
    }

    private String readJwt(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if ("JWT".equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
