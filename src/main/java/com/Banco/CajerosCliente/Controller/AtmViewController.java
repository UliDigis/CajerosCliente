package com.Banco.CajerosCliente.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Banco.CajerosCliente.Service.CajeroConsultaClientService;

@Controller
public class AtmViewController {

    private final CajeroConsultaClientService cajeroService;

    public AtmViewController(CajeroConsultaClientService cajeroService) {
        this.cajeroService = cajeroService;
    }
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
