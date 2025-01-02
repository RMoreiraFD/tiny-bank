package com.tiny.bank.api.model.request;

import java.time.LocalDate;

public record UserCreationRequest(String name, String ccNumber, LocalDate birthdate) {
}
