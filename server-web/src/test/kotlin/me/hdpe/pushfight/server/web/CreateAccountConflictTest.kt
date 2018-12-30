package me.hdpe.pushfight.server.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import me.hdpe.pushfight.server.web.accounts.CreateAccountRequest
import me.hdpe.pushfight.server.web.security.ClientDetails
import me.hdpe.pushfight.server.web.security.ClientDetailsProvider
import me.hdpe.pushfight.server.web.token.TokenRequest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@Configuration
class Config {

    @Bean
    fun clientDetailsProvider(writeState: WriteState): ClientDetailsProvider {
        return mock {
            on { clients } doReturn(listOf(ClientDetails("10000", "You API", "testAccessKeyId", "s3CrEt")))
        }
    }
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [
    WebConfig::class,
    Config::class
])
@TestPropertySource("classpath:/test-application.properties")
class CreateAccountConflictTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    private var token: String? = null

    @BeforeAll
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity()).build()
    }

    @Test
    fun testAccountConflict() {
        token()

        val req = CreateAccountRequest("x")

        mockMvc.perform(MockMvcRequestBuilders.post("/account").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.post("/account").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(MockMvcResultMatchers.status().isConflict)
    }

    private fun token() {
        val req = TokenRequest("testAccessKeyId", "s3CrEt")

        mockMvc.perform(MockMvcRequestBuilders.post("/token").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = false)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo { result ->
                    token = objectMapper.readValue(result.response.contentAsString,
                            ObjectNode::class.java)["token"].asText()
                }
    }

    private fun headers(content: Boolean = false, authorised: Boolean = false,
                        token: String? = null): RequestPostProcessor = RequestPostProcessor {
        if (authorised) {
            it.addHeader("Authorization", "Bearer ${token ?: this.token}")
        }
        it.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        if (content) {
            it.addHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        }
        return@RequestPostProcessor it
    }
}