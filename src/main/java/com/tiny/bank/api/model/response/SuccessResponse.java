package com.tiny.bank.api.model.response;

public record SuccessResponse(String message) implements GenericResponse {

    private static final String SUCCESS_MESSAGE = "Operation successful";
}
