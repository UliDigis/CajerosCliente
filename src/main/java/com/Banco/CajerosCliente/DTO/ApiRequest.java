package com.Banco.CajerosCliente.DTO;

import java.util.Map;

public class ApiRequest {

    private Map<String, Object> data;

    public ApiRequest() {
    }

    public ApiRequest(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
