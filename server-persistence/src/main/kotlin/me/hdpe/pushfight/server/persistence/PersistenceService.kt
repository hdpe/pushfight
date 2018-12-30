package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.server.persistence.account.AccountPersistence
import me.hdpe.pushfight.server.persistence.game.GamePersistence

interface PersistenceService : GamePersistence, AccountPersistence
