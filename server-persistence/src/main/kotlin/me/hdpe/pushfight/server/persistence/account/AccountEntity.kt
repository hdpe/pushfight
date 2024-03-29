package me.hdpe.pushfight.server.persistence.account

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "accounts")
class AccountEntity(
        @Id
        var id: String,

        @Column(name = "username")
        var username: String
) {
    @Column(name = "normalized_username")
    var normalizedUsername: String = username.lowercase()

    @Column(name = "exclude_from_statistics")
    var excludeFromStatistics: Boolean = false
}