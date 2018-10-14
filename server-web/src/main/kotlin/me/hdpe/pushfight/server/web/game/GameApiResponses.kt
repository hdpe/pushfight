package me.hdpe.pushfight.server.web.game

import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import me.hdpe.pushfight.server.web.WebSwaggerConfig

@ApiResponses(
        ApiResponse(code = 400, message = "Bad request", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 401, message = "Not authorized", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 403, message = "Forbidden", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 404, message = "Game not found", response = WebSwaggerConfig.ErrorResponse::class)
)
annotation class AuthenticationAndGameFoundRequiredRequestWithContentApiResponses {}

@ApiResponses(
        ApiResponse(code = 401, message = "Not authorized", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 403, message = "Forbidden", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 404, message = "Game not found", response = WebSwaggerConfig.ErrorResponse::class)
)
annotation class AuthenticationAndGameFoundRequiredRequestWithNoContentApiResponses {}