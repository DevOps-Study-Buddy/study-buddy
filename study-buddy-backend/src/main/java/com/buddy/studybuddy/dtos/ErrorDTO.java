package com.buddy.studybuddy.dtos;


public class ErrorDTO {
    private String code;
    private String message;

    public ErrorDTO() {

    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
