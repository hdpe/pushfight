package me.hdpe.pushfight.server.web.accounts.env

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import me.hdpe.pushfight.server.web.accounts.AccountDetails
import me.hdpe.pushfight.server.web.accounts.AccountDetailsProvider
import me.hdpe.pushfight.server.web.accounts.AccountProperties
import org.springframework.stereotype.Service

@Service
class EnvironmentAccountDetailsProvider(properties: AccountProperties) : AccountDetailsProvider {

    override val accounts: List<AccountDetails> = ObjectMapper().registerModule(KotlinModule())
            .readValue(properties.json)
}