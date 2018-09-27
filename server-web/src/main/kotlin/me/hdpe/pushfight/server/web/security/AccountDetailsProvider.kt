package me.hdpe.pushfight.server.web.security

interface AccountDetailsProvider {
    val accounts: List<AccountDetails>
}