package me.hdpe.pushfight.server.web.security.env

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import me.hdpe.pushfight.server.web.security.ClientDetails
import me.hdpe.pushfight.server.web.security.ClientDetailsProvider
import me.hdpe.pushfight.server.web.security.SecurityProperties

class EnvironmentClientDetailsProvider(properties: SecurityProperties) : ClientDetailsProvider {

    override val clients: List<ClientDetails> = ObjectMapper().registerModule(KotlinModule())
            .readValue(properties.clients)
}