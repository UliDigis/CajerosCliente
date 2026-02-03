package com.Banco.CajerosCliente.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.Banco.CajerosCliente.DTO.ApiResponse;

@Service
public class CajeroConsultaClientService {

    private final RestClient restClient;

    public CajeroConsultaClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Llama al backend (CajerosService) para obtener el listado de cajeros con saldo.
     * Se retorna una lista de mapas porque la respuesta del service usa DTO genérico.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerCajeros(String token) {
        ApiResponse resp = restClient.get()
                .uri("/cajeros")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ApiResponse.class);

        if (resp == null || !resp.isSuccess() || resp.getData() == null) {
            return Collections.emptyList();
        }

        return (List<Map<String, Object>>) resp.getData();
    }
}
