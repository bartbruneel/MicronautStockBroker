package com.bartbruneel.models;

import java.math.BigDecimal;
import java.util.UUID;

public record Wallet (
    UUID accountId,
    UUID walletId,
    Symbol symbol,
    BigDecimal available,
    BigDecimal locked
) implements RestApiResponse {

    public Wallet addAvailable(BigDecimal amountToAdd) {
        return new Wallet(
                accountId,
                walletId,
                symbol,
                available.add(amountToAdd),
                this.locked
        );
    }

    public Wallet withdrawAvailable(BigDecimal amountToWithdraw) {
        return new Wallet(
                accountId,
                walletId,
                symbol,
                available.subtract(amountToWithdraw),
                this.locked
        );
    }
}
