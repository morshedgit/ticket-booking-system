package yar.sam.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiResponse<T> {
    private T data;
    private String message;
    private Long timestamp;

    // Fields used internally by CustomResponseFilter
    @JsonIgnore
    private int statusCode;
    @JsonIgnore
    private Map<String, String> headers;

    public ApiResponse(T data, String message, int statusCode, Map<String, String> headers) {
        this.data = data;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.statusCode = statusCode;
        this.headers = headers;
    }

    // Getters and setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    // Methods for CustomResponseFilter
    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

