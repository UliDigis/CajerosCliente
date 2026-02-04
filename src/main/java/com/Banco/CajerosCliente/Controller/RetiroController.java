package com.Banco.CajerosCliente.Controller;

import com.Banco.CajerosCliente.DTO.ApiResponse;
import com.Banco.CajerosCliente.DTO.RetiroResponse;
import com.Banco.CajerosCliente.Service.RetiroClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/retiro")
public class RetiroController {

    private final RetiroClientService retiroClientService;

    public RetiroController(RetiroClientService retiroClientService) {
        this.retiroClientService = retiroClientService;
    }

    @PostMapping
    public ResponseEntity<?> realizar(
            @RequestParam("codigoCajero") String codigoCajero,
            @RequestParam("numeroTarjeta") String numeroTarjeta,
            @RequestParam("nip") String nip,
            @RequestParam("monto") long monto,
            HttpServletRequest request) {

        try {
            String token = readJwt(request);
            long montoCentavos = monto * 100; // Convertir a centavos

            RetiroResponse retiro = retiroClientService.ejecutarRetiro(
                    codigoCajero, numeroTarjeta, nip, montoCentavos, token);

            ApiResponse res = new ApiResponse();
            res.setSuccess(true);
            res.setData(retiro);

            return ResponseEntity.ok(res);

        } catch (Exception ex) {
            ApiResponse res = new ApiResponse();
            res.setSuccess(false);
            res.setError(ex.getMessage());

            return ResponseEntity.badRequest().body(res);
        }
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
