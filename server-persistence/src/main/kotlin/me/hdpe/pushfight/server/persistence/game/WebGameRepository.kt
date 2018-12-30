package me.hdpe.pushfight.server.persistence.game

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WebGameRepository : CrudRepository<WebGameEntity, String> {

    @Query("from #{#entityName} where (player1AccountId = ?1 or player2AccountId = ?1) and victorAccountId is null")
    fun findAllActiveByAccountId(accountId: String): List<WebGameEntity>
}