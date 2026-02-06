package com.Banco.CajerosCliente.Service;

import com.Banco.CajerosCliente.DTO.ApiRequest;
import com.Banco.CajerosCliente.DTO.ApiResponse;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AtmClientService {

    private final RestClient restClient;

    public AtmClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ApiResponse autenticarTarjeta(String numeroTarjeta, String nip, String token) {
        ApiRequest req = new ApiRequest(Map.of(
                "tarjeta", numeroTarjeta,
                "nip", nip
        ));

        RestClient.RequestHeadersSpec<?> spec = restClient.post()
                .uri("/atm/autenticar")
                .body(req);

        if (token != null && !token.isBlank()) {
            spec = spec.header("Authorization", "Bearer " + token);
        }

        return spec.retrieve().body(ApiResponse.class);
    }

    public ApiResponse consultarSaldo(long idCuenta, String token) {
        RestClient.RequestHeadersSpec<?> spec = restClient.get()
                .uri("/atm/saldo/{idCuenta}", idCuenta);

        if (token != null && !token.isBlank()) {
            spec = spec.header("Authorization", "Bearer " + token);
        }

        return spec.retrieve().body(ApiResponse.class);
    }
}
