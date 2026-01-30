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
        restClient.post()
                .uri("/admin/cajeros/{codigo}/recargar", codigo)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * CLIENTE: llama al SERVICE para recargar todos los cajeros.
     */
    public void recargarTodos(String token) {
        restClient.post()
                .uri("/admin/cajeros/recargar-todos")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }
}
