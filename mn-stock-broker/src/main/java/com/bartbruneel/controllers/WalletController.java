package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryAccountStore;
import com.bartbruneel.errors.CustomError;
import com.bartbruneel.errors.FiatCurrencyNotSupportedException;
import com.bartbruneel.models.DepositFiatMoney;
import com.bartbruneel.models.RestApiResponse;
import com.bartbruneel.models.Wallet;
import com.bartbruneel.models.WithdrawFiatMoney;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static com.bartbruneel.data.InMemoryAccountStore.ACCOUNT_ID;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/account/wallets")
public record WalletController(InMemoryAccountStore store) {


    private static final Logger LOG = LoggerFactory.getLogger(WalletController.class);

    public static final List<String> SUPPORTED_FIAT_CURRENCIES = List.of("EUR", "USD", "CHF", "GBP");

    @Get(produces = MediaType.APPLICATION_JSON)
    public Collection<Wallet> get() {
        return store.getWalletsCollection(ACCOUNT_ID);}

    @Post(
            value = "/deposit",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<RestApiResponse> depositFiatMoney(@Body DepositFiatMoney deposit) {
        // Option 1: Custom HttpResponse
        if(!SUPPORTED_FIAT_CURRENCIES.contains(deposit.symbol().value())) {
            return HttpResponse.badRequest()
                    .body(new CustomError(HttpStatus.BAD_REQUEST.getCode(),
                            "UNSUPPORTED_FIAT_CURRENCY",
                            String.format("Only %s are supported", SUPPORTED_FIAT_CURRENCIES)));
        }
        var wallet = store.depositToWallet(deposit);
        LOG.debug("Wallet after Deposit: {}", wallet);
        return HttpResponse.ok().body(wallet);
    }

    @Post(
            value = "/withdraw",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public Wallet withdawFiatMoney(@Body WithdrawFiatMoney withdraw) {
        if(!SUPPORTED_FIAT_CURRENCIES.contains(withdraw.symbol().value())) {
            throw new FiatCurrencyNotSupportedException(String.format("Only %s are supported", SUPPORTED_FIAT_CURRENCIES));
        }
        var wallet = store.withdrawalFromWallet(withdraw);
        LOG.debug("Wallet after Withdrawal: {}", wallet);
        return wallet;
    }
}
