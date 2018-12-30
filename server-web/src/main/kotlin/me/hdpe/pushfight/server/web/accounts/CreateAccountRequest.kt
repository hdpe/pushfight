package me.hdpe.pushfight.server.web.accounts

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotNull

class CreateAccountRequest(
        @get:NotNull(message = "{NotNull.account.username}")
        @get:Length(message = "{Length.account.username}", min = 1, max = 255)
        val username: String?
)
