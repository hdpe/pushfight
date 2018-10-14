package me.hdpe.pushfight.server.persistence

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "games")
class WebGameEntity(
        @Id
        var id: String,

        var state: String
) {
}