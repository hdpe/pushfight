package me.hdpe.pushfight.server.persistence.account;

class AccountWithStatisticsDetails(
        val id: String,
        val name: String,
        val played: Int,
        val won: Int,
        val lost: Int
)