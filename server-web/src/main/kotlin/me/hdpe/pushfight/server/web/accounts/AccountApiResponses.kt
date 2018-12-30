package me.hdpe.pushfight.server.web.accounts

import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import me.hdpe.pushfight.server.web.WebSwaggerConfig

@ApiResponses(
        ApiResponse(code = 401, message = "Not authorized", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 403, message = "Forbidden", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 404, message = "Account not found", response = WebSwaggerConfig.ErrorResponse::class)
)
annotation class AuthenticationAndAccountFoundRequiredRequestWithNoContentApiResponses {}