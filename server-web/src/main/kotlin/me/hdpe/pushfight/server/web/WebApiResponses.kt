package me.hdpe.pushfight.server.web

import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses

@ApiResponses(
        ApiResponse(code = 400, message = "Bad request", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 401, message = "Not authorized", response = WebSwaggerConfig.ErrorResponse::class)
)
annotation class AuthenticationEndpointRequestApiResponses {}

@ApiResponses(
        ApiResponse(code = 400, message = "Bad request", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 401, message = "Not authorized", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 403, message = "Forbidden", response = WebSwaggerConfig.ErrorResponse::class)
)
annotation class AuthenticationRequiredRequestWithContentApiResponses {}

@ApiResponses(
        ApiResponse(code = 401, message = "Not authorized", response = WebSwaggerConfig.ErrorResponse::class),
        ApiResponse(code = 403, message = "Forbidden", response = WebSwaggerConfig.ErrorResponse::class)
)
annotation class AuthenticationRequiredRequestWithNoContentApiResponses {}
