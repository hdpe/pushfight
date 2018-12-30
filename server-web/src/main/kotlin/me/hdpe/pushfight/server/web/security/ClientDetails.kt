package me.hdpe.pushfight.server.web.security

class ClientDetails(val id: String, val name: String, val accessKeyId: String, var secret: String?, val fixedAccountId: String? = null)
