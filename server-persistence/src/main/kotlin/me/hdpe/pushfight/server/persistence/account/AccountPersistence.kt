package me.hdpe.pushfight.server.persistence.account

interface AccountPersistence {

    fun getAccount(username: String): AccountDetails?;

    fun getAccountById(id: String): AccountDetails?;

    fun getActiveAccounts(): List<AccountWithStatisticsDetails>

    fun createAccount(command: CreateAccountCommand): AccountDetails
}
