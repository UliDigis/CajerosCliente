package com.Banco.CajerosCliente.DTO;

public class ApiResponse {

    private boolean success;
    private Object data;
    private Object error;

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public Object getError() {
        return error;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
