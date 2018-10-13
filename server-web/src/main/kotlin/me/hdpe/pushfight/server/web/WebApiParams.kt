package me.hdpe.pushfight.server.web

import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams

@ApiImplicitParams(
        ApiImplicitParam(paramType = "header", name = "Authorization", value = "Bearer token", required = true)
)
annotation class AuthorizationHeaderRequired {}