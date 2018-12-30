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
)