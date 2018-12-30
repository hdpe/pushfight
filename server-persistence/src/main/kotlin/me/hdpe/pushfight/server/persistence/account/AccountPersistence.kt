package me.hdpe.pushfight.server.persistence.account

interface AccountPersistence {

    fun getAccount(username: String): AccountDetails;

    fun getActiveAccounts(): List<AccountDetails>

    fun createAccount(command: CreateAccountCommand): AccountDetails
}
