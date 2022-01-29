package com.bartbruneel.models;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawFiatMoney(UUID accountId,
                                UUID walletId,
                                Symbol symbol,
                                BigDecimal amount) {
}
