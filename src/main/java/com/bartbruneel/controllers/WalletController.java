package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryAccountStore;
import com.bartbruneel.data.InMemoryStore;
import com.bartbruneel.errors.CustomError;
import com.bartbruneel.models.DepositFiatMoney;
import com.bartbruneel.models.Wallet;
import com.bartbruneel.models.WithdrawFiatMoney;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import java.util.Collection;
import java.util.List;

import static com.bartbruneel.data.InMemoryAccountStore.ACCOUNT_ID;

@Controller("/account/wallets")
public record WalletController(InMemoryAccountStore store) {

    public static final List<String> SUPPORTED_FIAT_CURRENCIES = List.of("EUR", "USD", "CHF", "GBP");

    @Get(produces = MediaType.APPLICATION_JSON)
    public Collection<Wallet> get() {
        return store.getWallets(ACCOUNT_ID);}

    @Post(
            value = "/deposit",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<CustomError> depositFiatMoney(@Body DepositFiatMoney deposit) {
        // Option 1: Custom HttpResponse
        if(!SUPPORTED_FIAT_CURRENCIES.contains(deposit.symbol().value())) {
            return HttpResponse.badRequest()
                    .body(new CustomError(HttpStatus.BAD_REQUEST.getCode(),
                            "UNSUPPORTED_FIAT_CURRENCY",
                            String.format("Only %s are supported", SUPPORTED_FIAT_CURRENCIES)));
        }
        return HttpResponse.ok();
    }

    @Post(
            value = "/withdraw",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public void withdawFiatMoney(@Body WithdrawFiatMoney withdraw) {
        // Option 2: Custom Error Processing
    }
}
