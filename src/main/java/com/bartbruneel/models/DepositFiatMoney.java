package com.bartbruneel.models;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositFiatMoney(UUID accountId,
                               UUID walletId,
                               Symbol symbol,
                               BigDecimal amount) {
}
