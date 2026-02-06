package com.Banco.CajerosCliente.Service;

import com.Banco.CajerosCliente.DTO.ApiRequest;
import com.Banco.CajerosCliente.DTO.ApiResponse;
import com.Banco.CajerosCliente.DTO.RetiroResponse;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RetiroClientService {

    private final RestClient restClient;

    public RetiroClientService(@Value("${service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Llama al endpoint /atm/retirar del SERVICE para ejecutar un retiro
     */
    public RetiroResponse ejecutarRetiro(String codigoCajero, String numeroTarjeta, 
                                         String nip, long montoCentavos, String token) {

        ApiRequest req = new ApiRequest(Map.of(
                "codigoCajero", codigoCajero,
                // El Service espera "tarjeta". Mandamos también "numeroTarjeta" por compatibilidad.
                "tarjeta", numeroTarjeta,
                "numeroTarjeta", numeroTarjeta,
                "nip", nip,
                "montoCentavos", montoCentavos
        ));

        RestClient.RequestHeadersSpec<?> requestSpec = restClient.post()
                .uri("/atm/retirar")
                .body(req);

        if (token != null && !token.isBlank()) {
            requestSpec = requestSpec.header("Authorization", "Bearer " + token);
        }

        ApiResponse res = requestSpec.retrieve().body(ApiResponse.class);

        if (res == null) {
            throw new RuntimeException("Respuesta nula del servicio de retiros");
        }
        if (!res.isSuccess()) {
            throw new RuntimeException(extractErrorMessage(res.getError(), "Operación de retiro rechazada"));
        }
        if (res.getData() == null) {
            throw new RuntimeException("Respuesta sin datos de retiro");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) res.getData();

        Number saldoRestanteNum = (Number) data.get("saldoRestanteCentavos");
        if (saldoRestanteNum == null) {
            throw new RuntimeException("Falta saldoRestanteCentavos en la respuesta");
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> desglose = (List<Map<String, Object>>) data.get("desglose");

        RetiroResponse retiro = new RetiroResponse(saldoRestanteNum.longValue(), desglose);

        Number montoNum = (Number) data.get("montoCentavos");
        if (montoNum != null) {
            retiro.setMontoCentavos(montoNum.longValue());
        }

        return retiro;
    }

    @SuppressWarnings("unchecked")
    private static String extractErrorMessage(Object error, String fallback) {
        if (error == null) {
            return fallback;
        }
        if (error instanceof Map<?, ?> map) {
            Object msg = ((Map<String, Object>) map).get("message");
            if (msg != null) {
                return msg.toString();
            }
        }
        return error.toString();
    }
}
