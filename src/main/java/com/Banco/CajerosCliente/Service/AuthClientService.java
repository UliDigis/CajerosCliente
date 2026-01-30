package com.Banco.CajerosCliente.Service;

import com.Banco.CajerosCliente.DTO.ApiRequest;
import com.Banco.CajerosCliente.DTO.ApiResponse;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthClientService {

    private final RestClient restClient;

    public AuthClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    /** 
     * CLIENTE: llama al SERVICE para autenticar y devuelve el JWT.
     */
    public String login(String correo, String password) {

        ApiRequest req = new ApiRequest(Map.of(
                "correo", correo,
                "password", password
        ));

        ApiResponse res = restClient.post()
                .uri("/auth/login")
                .body(req)
                .retrieve()
                .body(ApiResponse.class);

        if (res == null || !res.isSuccess() || res.getData() == null) {
            throw new RuntimeException("Credenciales inválidas");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) res.getData();

        Object token = data.get("token");
        if (token == null) {
            throw new RuntimeException("Respuesta inválida del Service");
        }

        return token.toString();
    }
}
