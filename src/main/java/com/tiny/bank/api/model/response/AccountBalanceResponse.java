package com.tiny.bank.api.model.response;

import java.math.BigDecimal;

public record AccountBalanceResponse(BigDecimal value, String accountId, String userId) {
}
