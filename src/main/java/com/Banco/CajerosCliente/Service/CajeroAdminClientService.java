package com.Banco.CajerosCliente.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CajeroAdminClientService {

    private final RestClient restClient;

    public CajeroAdminClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * CLIENTE: llama al SERVICE para recargar un cajero.
     */
    public void recargarCajero(String codigo, String token) {

        RestClient.RequestHeadersSpec<?> req = restClient.post()
                .uri("/admin/cajeros/{codigo}/recargar", codigo);

        if (token != null && !token.isBlank()) {
            req = req.header("Authorization", "Bearer " + token);
        }

        req.retrieve().toBodilessEntity();
    }

    public void recargarTodos(String token) {

        RestClient.RequestHeadersSpec<?> req = restClient.post()
                .uri("/admin/cajeros/recargar-todos");

        if (token != null && !token.isBlank()) {
            req = req.header("Authorization", "Bearer " + token);
        }

        req.retrieve().toBodilessEntity();
    }

}
