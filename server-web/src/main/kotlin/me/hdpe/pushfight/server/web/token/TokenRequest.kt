package me.hdpe.pushfight.server.web.token

import javax.validation.constraints.NotNull

class TokenRequest(@get:NotNull(message = "{NotNull.accessKeyId}") var accessKeyId: String?,
                   @get:NotNull(message = "{NotNull.secret}") var secret: String?)