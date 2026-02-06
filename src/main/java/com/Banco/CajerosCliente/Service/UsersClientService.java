package com.Banco.CajerosCliente.Service;

import com.Banco.CajerosCliente.DTO.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UsersClientService {

    private final RestClient restClient;

    public UsersClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Consulta el endpoint GET /usuarios del Service.
     * El token es opcional (el Service permite sin token).
     */
    public ApiResponse obtenerUsuarios(String token) {
        RestClient.RequestHeadersSpec<?> spec = restClient.get().uri("/usuarios");

        if (token != null && !token.isBlank()) {
            spec = spec.header("Authorization", "Bearer " + token);
        }

        return spec.retrieve().body(ApiResponse.class);
    }
}

