package com.Banco.CajerosCliente.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class dashboardController {

    @GetMapping("dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("atms")
    public String atms() {
        return "atm";
    }
//    @GetMapping("users")
//    public String cajas() {
//        return "user";
//    }
}
