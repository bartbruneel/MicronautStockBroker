package com.bartbruneel.data;

import com.bartbruneel.models.DepositFiatMoney;
import com.bartbruneel.models.Symbol;
import com.bartbruneel.models.Wallet;
import com.bartbruneel.models.WatchList;
import com.bartbruneel.models.WithdrawFiatMoney;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {

    public static final UUID ACCOUNT_ID = UUID.fromString("991fabad-02cc-4edd-93f1-b8f3677a7e98");
    public static final UUID WALLET_ID = UUID.fromString("991fabad-02cc-4edd-93f1-b8f3677a7e98");


    private final Map<UUID, WatchList> watchListsPerAccount = new HashMap<>();
    private final Map<UUID, Map<UUID, Wallet>> walletsPerAccount = new HashMap<>();

    public WatchList getWatchList(final UUID accountId) {
        return watchListsPerAccount.getOrDefault(accountId, new WatchList());
    }

    public WatchList updateWatchList(final UUID accountId, final WatchList watchList) {
        watchListsPerAccount.put(accountId, watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(final UUID accountId) {
        watchListsPerAccount.remove(accountId);
    }


    public Wallet depositToWallet(DepositFiatMoney deposit) {
        Map<UUID, Wallet> wallets = getWalletsMap(deposit.accountId());
        var oldWallet = getOldWallet(deposit.walletId(), deposit.symbol(), wallets);
        var newWallet = oldWallet.addAvailable(deposit.amount());
        wallets.put(newWallet.walletId(), newWallet);
        walletsPerAccount.put(newWallet.accountId(), wallets);
        return newWallet;
    }

    public Wallet withdrawalFromWallet(WithdrawFiatMoney withdraw) {
        Map<UUID, Wallet> wallets = getWalletsMap(withdraw.accountId());
        var oldWallet = getOldWallet(withdraw.walletId(), withdraw.symbol(), wallets);
        var newWallet = oldWallet.withdrawAvailable(withdraw.amount());
        wallets.put(newWallet.walletId(), newWallet);
        walletsPerAccount.put(newWallet.accountId(), wallets);
        return newWallet;
    }

    public Map<UUID, Wallet> getWalletsMap(UUID accountId) {
        return Optional.ofNullable(walletsPerAccount.get(accountId))
                .orElse(new HashMap<>());
    }

    public Collection<Wallet> getWalletsCollection(UUID accountId) {
        return walletsPerAccount.get(accountId).values();
    }

    private Wallet getOldWallet(UUID walletId, Symbol symbol, Map<UUID, Wallet> wallets) {
        return Optional.ofNullable(wallets.get(walletId))
                .orElse(new Wallet(ACCOUNT_ID, walletId, symbol, BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
