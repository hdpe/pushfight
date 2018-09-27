package me.hdpe.pushfight.server.web.security.env

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import me.hdpe.pushfight.server.web.security.AccountDetails
import me.hdpe.pushfight.server.web.security.AccountDetailsProvider
import me.hdpe.pushfight.server.web.security.SecurityProperties
import org.springframework.stereotype.Service

@Service
class EnvironmentAccountDetailsProvider(properties: SecurityProperties) : AccountDetailsProvider {

    override val accounts: List<AccountDetails> = ObjectMapper().registerModule(KotlinModule())
            .readValue(properties.accounts)
}