package me.hdpe.pushfight.server.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface WebGameRepository : CrudRepository<WebGameEntity, String> {
}