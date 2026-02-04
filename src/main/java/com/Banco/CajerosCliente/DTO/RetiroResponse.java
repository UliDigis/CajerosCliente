package com.Banco.CajerosCliente.DTO;

import java.util.List;
import java.util.Map;

/**
 * Representa la respuesta de un retiro del servicio backend.
 */
public class RetiroResponse {

    private long saldoRestanteCentavos;
    private List<Map<String, Object>> desglose;
    private long montoCentavos;

    public RetiroResponse() {
    }

    public RetiroResponse(long saldoRestanteCentavos, List<Map<String, Object>> desglose) {
        this.saldoRestanteCentavos = saldoRestanteCentavos;
        this.desglose = desglose;
    }

    public long getSaldoRestanteCentavos() {
        return saldoRestanteCentavos;
    }

    public void setSaldoRestanteCentavos(long saldoRestanteCentavos) {
        this.saldoRestanteCentavos = saldoRestanteCentavos;
    }

    public List<Map<String, Object>> getDesglose() {
        return desglose;
    }

    public void setDesglose(List<Map<String, Object>> desglose) {
        this.desglose = desglose;
    }

    public long getMontoCentavos() {
        return montoCentavos;
    }

    public void setMontoCentavos(long montoCentavos) {
        this.montoCentavos = montoCentavos;
    }
}
