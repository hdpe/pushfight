package me.hdpe.pushfight.server.web.accounts

class AccountWithStatisticsResult(
        val id: String,
        val name: String,
        val played: Int,
        val won: Int,
        val lost: Int
)