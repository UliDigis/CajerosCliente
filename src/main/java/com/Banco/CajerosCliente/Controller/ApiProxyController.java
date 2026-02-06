package com.Banco.CajerosCliente.Controller;

import com.Banco.CajerosCliente.DTO.ApiRequest;
import com.Banco.CajerosCliente.DTO.ApiResponse;
import com.Banco.CajerosCliente.DTO.RetiroResponse;
import com.Banco.CajerosCliente.Service.AtmClientService;
import com.Banco.CajerosCliente.Service.CajeroAdminClientService;
import com.Banco.CajerosCliente.Service.CajeroConsultaClientService;
import com.Banco.CajerosCliente.Service.RetiroClientService;
import com.Banco.CajerosCliente.Service.UsersClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiProxyController {

    private final CajeroConsultaClientService cajeroConsultaClientService;
    private final CajeroAdminClientService cajeroAdminClientService;
    private final RetiroClientService retiroClientService;
    private final AtmClientService atmClientService;
    private final UsersClientService usersClientService;

    public ApiProxyController(
            CajeroConsultaClientService cajeroConsultaClientService,
            CajeroAdminClientService cajeroAdminClientService,
            RetiroClientService retiroClientService,
            AtmClientService atmClientService,
            UsersClientService usersClientService
    ) {
        this.cajeroConsultaClientService = cajeroConsultaClientService;
        this.cajeroAdminClientService = cajeroAdminClientService;
        this.retiroClientService = retiroClientService;
        this.atmClientService = atmClientService;
        this.usersClientService = usersClientService;
    }

    @GetMapping("/cajeros")
    public ResponseEntity<ApiResponse> cajeros(HttpServletRequest request) {
        String token = readJwtOrAuthHeader(request);

        List<Map<String, Object>> data = cajeroConsultaClientService.obtenerCajeros(token);
        ApiResponse res = ok(data);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse> usuarios(HttpServletRequest request) {
        try {
            String token = readJwtOrAuthHeader(request);
            ApiResponse res = usersClientService.obtenerUsuarios(token);
            if (res == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(err("Respuesta nula del Service"));
            }
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(err(ex.getMessage()));
        }
    }

    @PostMapping("/admin/cajeros/{codigo}/recargar")
    public ResponseEntity<ApiResponse> recargarUno(@PathVariable("codigo") String codigo, HttpServletRequest request) {
        try {
            String token = readJwtOrAuthHeader(request);
            cajeroAdminClientService.recargarCajero(codigo, token);
            return ResponseEntity.ok(ok(Map.of("codigoCajero", codigo)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(err(ex.getMessage()));
        }
    }

    @PostMapping("/admin/cajeros/recargar-todos")
    public ResponseEntity<ApiResponse> recargarTodos(HttpServletRequest request) {
        try {
            String token = readJwtOrAuthHeader(request);
            cajeroAdminClientService.recargarTodos(token);
            return ResponseEntity.ok(ok(Map.of("status", "ok")));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(err(ex.getMessage()));
        }
    }

    @PostMapping("/atm/autenticar")
    public ResponseEntity<ApiResponse> autenticar(@RequestBody(required = false) ApiRequest body,
            HttpServletRequest request) {
        try {
            Map<String, Object> data = body != null ? body.getData() : null;
            String numeroTarjeta = str(get(data, "numeroTarjeta"));
            if (numeroTarjeta == null || numeroTarjeta.isBlank()) {
                numeroTarjeta = str(get(data, "tarjeta"));
            }
            String nip = str(get(data, "nip"));

            if (numeroTarjeta == null || numeroTarjeta.isBlank() || nip == null || nip.isBlank()) {
                return ResponseEntity.badRequest().body(err("Faltan datos: numeroTarjeta/tarjeta y nip"));
            }

            // Nuevo flujo del Service: /atm/autenticar devuelve un JWT propio de ATM,
            // no requiere token entrante.
            ApiResponse res = atmClientService.autenticarTarjeta(numeroTarjeta, nip, null);

            if (res == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(err("Respuesta nula del Service"));
            }

            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(err(ex.getMessage()));
        }
    }

    @GetMapping("/atm/saldo/{idCuenta}")
    public ResponseEntity<ApiResponse> saldo(@PathVariable("idCuenta") long idCuenta, HttpServletRequest request) {
        try {
            // Para endpoints /atm/** preferimos el Authorization header (token ATM)
            // sobre la cookie JWT (login web).
            String token = readJwtOrAuthHeaderPreferAuth(request);
            ApiResponse res = atmClientService.consultarSaldo(idCuenta, token);
            if (res == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(err("Respuesta nula del Service"));
            }
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(err(ex.getMessage()));
        }
    }

    @PostMapping("/atm/retirar")
    public ResponseEntity<ApiResponse> retirar(@RequestBody(required = false) ApiRequest body,
            HttpServletRequest request) {
        try {
            Map<String, Object> data = body != null ? body.getData() : null;

            String codigoCajero = str(get(data, "codigoCajero"));
            String numeroTarjeta = str(get(data, "numeroTarjeta"));
            if (numeroTarjeta == null || numeroTarjeta.isBlank()) {
                numeroTarjeta = str(get(data, "tarjeta"));
            }
            String nip = str(get(data, "nip"));
            Long montoCentavos = num(get(data, "montoCentavos"));

            if (codigoCajero == null || codigoCajero.isBlank()
                    || numeroTarjeta == null || numeroTarjeta.isBlank()
                    || nip == null || nip.isBlank()
                    || montoCentavos == null || montoCentavos <= 0) {
                return ResponseEntity.badRequest().body(err("Faltan datos: codigoCajero, numeroTarjeta/tarjeta, nip, montoCentavos"));
            }

            String token = readJwtOrAuthHeaderPreferAuth(request);
            RetiroResponse retiro = retiroClientService.ejecutarRetiro(
                    codigoCajero, numeroTarjeta, nip, montoCentavos, token);
            return ResponseEntity.ok(ok(retiro));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(err(ex.getMessage()));
        }
    }

    private static ApiResponse ok(Object data) {
        ApiResponse res = new ApiResponse();
        res.setSuccess(true);
        res.setData(data);
        return res;
    }

    private static ApiResponse err(Object error) {
        ApiResponse res = new ApiResponse();
        res.setSuccess(false);
        res.setError(error != null ? error : "Error");
        return res;
    }

    private static Object get(Map<String, Object> data, String key) {
        return data != null ? data.get(key) : null;
    }

    private static String str(Object v) {
        return v != null ? v.toString() : null;
    }

    private static Long num(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.valueOf(v.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    private String readJwtOrAuthHeader(HttpServletRequest request) {
        // Cookie JWT (preferido)
        String token = readCookie(request, "JWT");
        if (token != null && !token.isBlank()) {
            return token;
        }

        // Authorization: Bearer <token> (opcional)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private String readJwtOrAuthHeaderPreferAuth(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return readCookie(request, "JWT");
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
