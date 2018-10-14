package me.hdpe.pushfight.server.persistence

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "games")
class WebGameEntity(
        @Id
        var id: String,

        @Column(name = "player1_account_id")
        var player1AccountId: String,

        @Column(name = "player2_account_id")
        var player2AccountId: String,

        var state: String,

        @Column(nullable = true)
        var victorAccountId: String?
)