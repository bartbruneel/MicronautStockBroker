package com.bartbruneel.models;

import java.math.BigDecimal;
import java.util.UUID;

public record Wallet (
    UUID accountId,
    UUID walletId,
    Symbol symbol,
    BigDecimal available,
    BigDecimal locked
) {}
