package me.hdpe.pushfight.server.web.token

import javax.validation.constraints.NotBlank

class TokenRequest(@get:NotBlank(message = "{NotBlank.tokenRequest.accessKeyId}") var accessKeyId: String?,
                   @get:NotBlank(message = "{NotBlank.tokenRequest.secret}") var secret: String?)