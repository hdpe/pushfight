package me.hdpe.pushfight.server.web.security

interface ClientDetailsProvider {
    val clients: List<ClientDetails>
}