package com.Banco.CajerosCliente.DTO;

public class RetiroRequest {
    private String codigoCajero;
    private String numeroTarjeta;
    private String nip;
    private long montoCentavos;

    public RetiroRequest() {}

    public RetiroRequest(String codigoCajero, String numeroTarjeta, String nip, long montoCentavos) {
        this.codigoCajero = codigoCajero;
        this.numeroTarjeta = numeroTarjeta;
        this.nip = nip;
        this.montoCentavos = montoCentavos;
    }

    public String getCodigoCajero() {
        return codigoCajero;
    }

    public void setCodigoCajero(String codigoCajero) {
        this.codigoCajero = codigoCajero;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public long getMontoCentavos() {
        return montoCentavos;
    }

    public void setMontoCentavos(long montoCentavos) {
        this.montoCentavos = montoCentavos;
    }
}
