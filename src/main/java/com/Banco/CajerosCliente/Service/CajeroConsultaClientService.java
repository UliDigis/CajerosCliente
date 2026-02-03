package com.Banco.CajerosCliente.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.Banco.CajerosCliente.DTO.ApiResponse;

@Service
public class CajeroConsultaClientService {

    private final RestClient restClient;

    public CajeroConsultaClientService(@Value("${service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Consulta el endpoint GET /cajeros del backend. Si token viene null/blank,
     * no se envía header Authorization.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerCajeros(String token) {

        RestClient.RequestHeadersSpec<?> req = restClient.get()
                .uri("/cajeros");

        if (token != null && !token.isBlank()) {
            req = req.header("Authorization", "Bearer " + token);
        }

        ApiResponse resp = req.retrieve().body(ApiResponse.class);

        if (resp == null || !resp.isSuccess() || resp.getData() == null) {
            return Collections.emptyList();
        }

        return (List<Map<String, Object>>) resp.getData();
    }

}
