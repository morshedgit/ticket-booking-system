package yar.sam.models;

public class ErrorInfo {
    private String message;
    private String errorCode;
    private String detail;

    public ErrorInfo(String message, String errorCode, String detail) {
        this.message = message;
        this.errorCode = errorCode;
        this.detail = detail;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDetail() {
        return detail;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
