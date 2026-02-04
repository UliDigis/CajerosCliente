package com.Banco.CajerosCliente.Controller;

import org.springframework.stereotype.Controller;

/**
 * Controlador auxiliar para vistas de ATM.
 * Las vistas se renderizan desde DashboardController.
 */
@Controller
public class AtmViewController {
    /**
     * Renderiza la vista de cajeros. En pruebas no se usa token.
     */
//    @GetMapping("/atms")
//    public String atms(Model model) {
//
//        List<Map<String, Object>> cajeros = cajeroService.obtenerCajeros(null);
//        model.addAttribute("cajeros", cajeros);
//
//        return "atm";
//    }
}
