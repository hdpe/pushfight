package me.hdpe.pushfight.server.web.accounts

interface AccountDetailsProvider {
    val accounts: List<AccountDetails>
}