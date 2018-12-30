package me.hdpe.pushfight.server.persistence.account

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository : CrudRepository<AccountEntity, String> {
    fun findByUsername(username: String): Optional<AccountEntity>;
}
