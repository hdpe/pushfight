package me.hdpe.pushfight.server.persistence.game

import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "games")
@EntityListeners(AuditingEntityListener::class)
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
) {
    @LastModifiedDate
    @Column(name = "last_modified")
    lateinit var lastModified: LocalDateTime
}