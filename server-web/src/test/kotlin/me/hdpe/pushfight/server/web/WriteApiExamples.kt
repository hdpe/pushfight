package me.hdpe.pushfight.server.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.base.Charsets
import me.hdpe.pushfight.server.web.accounts.CreateAccountRequest
import me.hdpe.pushfight.server.web.game.*
import me.hdpe.pushfight.server.web.token.TokenRequest
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.io.File

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [
    WebConfig::class,
    WriteConfig::class
])
@TestPropertySource("classpath:/test-application.properties")
class WriteApiExamples {

    @Value("#{systemProperties['examples.dir']?:'target/examples'}")
    private lateinit var examplesDir: File

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var writeState: WriteState

    private lateinit var mockMvc: MockMvc

    private lateinit var basePath: String

    private var token: String? = null

    private var gameId: String? = null

    @BeforeAll
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity()).build()

        mockMvc.perform(get("/v2/api-docs"))
                .andDo { result ->
                    basePath = objectMapper.readValue(result.response.contentAsString, ObjectNode::class.java)
                            .get("basePath").asText()
                }
    }

    @Test
    fun writeApiExamples() {
        token()

        createAccounts()
        getAccount()
        getAvailableOpponents()

        createGame()

        getGame()

        initialPlacements()
        updatePlacements()
        confirmSetup()

        doPlayer2Setup()

        doTurnMove()
        doTurnPush()

        getActiveGames()

        resign()
    }

    private fun token(writeExample: Boolean = true) {
        val req = TokenRequest("testAccessKeyId", "s3CrEt")

        val actions = mockMvc.perform(post("/token").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = false)))
                .andExpect(status().isOk)
                .andDo { result -> token = objectMapper.readValue(result.response.contentAsString,
                        ObjectNode::class.java)["token"].asText()}

        if (writeExample) {
            actions
                    .andDo { result -> writeRequestBody("Example Request", "token", result) }
                    .andDo { result -> writeResponseBody("Example Response", "token", result) }
                    .andDo {
                        write("Example Authorization Header (for inclusion in all subsequent requests)",
                                "token", "subsequent-req", "Authorization: Bearer $token")
                    }
        }
    }

    private fun createAccounts() {
        writeState.accountIdsByName = ExampleAccountNames.ACCOUNTS
                .mapIndexed { i, name -> Pair(name, createAccount(name, i == 0)) }
                .associate { it }

        // re-login, as we've altered the fixedAccountId for our client
        token(writeExample = false)
    }

    private fun createAccount(username: String, writeExample: Boolean): String {
        val req = CreateAccountRequest(username)

        var actions = mockMvc.perform(post("/account").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)

        if (writeExample) {
            actions = actions
                    .andDo { result -> writeRequestBody("Example Request", "createAccount", result) }
                    .andDo { result -> writeResponseBody("Example Response", "createAccount", result) }
        }

        var accountId: String? = null

        actions.andDo { result ->
            accountId = objectMapper.readValue(result.response.contentAsString,
                    ObjectNode::class.java)["id"].asText()
        }

        return accountId!!
    }

    private fun getAccount() {
        mockMvc.perform(get("/account").param("username", ExampleAccountNames.YOU)
                .with(headers(content = false, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestUri("Example Request", "getAccount", result) }
                .andDo { result -> writeResponseBody("Example Response", "getAccount", result) }
    }

    private fun getAvailableOpponents() {
        mockMvc.perform(get("/accounts/opponents")
                .with(headers(content = false, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestUri("Example Request", "getAvailableOpponents", result) }
                .andDo { result -> writeResponseBody("Example Response", "getAvailableOpponents", result) }
    }

    private fun createGame() {
        val req = CreateGameRequest(opponent = writeState.accountIdsByName[ExampleAccountNames.ADVERSARY1])

        mockMvc.perform(post("/game").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "createGame", result) }
                .andDo { result -> writeResponseBody("Example Response", "createGame", result) }
                .andDo { result -> gameId = objectMapper.readValue(result.response.contentAsString,
                        ObjectNode::class.java)["id"].asText()}
    }

    private fun getGame() {
        mockMvc.perform(get("/game/{gameId}", gameId)
                .with(headers(content = false, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestUri("Example Request", "getGame", result) }
                .andDo { result -> writeResponseBody("Example Response", "getGame", result) }
    }

    private fun initialPlacements() {
        val req = InitialPlacementsRequest(1, arrayOf(
                InitialPlacement("king", 0, 3),
                InitialPlacement("pawn", 1, 3),
                InitialPlacement("pawn", 2, 3),
                InitialPlacement("king", 3, 3),
                InitialPlacement("king", 2, 2)
        ))

        mockMvc.perform(post("/game/{gameId}/setup", gameId).content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "initialPlacements", result) }
                .andDo { result -> writeResponseBody("Example Response", "initialPlacements", result) }
    }

    private fun updatePlacements() {
        val req = UpdatePlacementsRequest(1, arrayOf(UpdatedPlacement(2, 2, 1, 2)))

        mockMvc.perform(patch("/game/{gameId}/setup", gameId).content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "updatePlacements", result) }
                .andDo { result -> writeResponseBody("Example Response", "updatePlacements", result) }
    }

    private fun confirmSetup() {
        val req = ConfirmSetupRequest(1)

        mockMvc.perform(post("/game/{gameId}/setup/confirm", gameId).content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "confirmSetup", result) }
                .andDo { result -> writeResponseBody("Example Response", "confirmSetup", result) }
    }

    private fun doPlayer2Setup() {
        val tokenReq = TokenRequest("testAccessKeyId2", "s3CrEt2")
        var player2Token: String? = null

        mockMvc.perform(post("/token").content(objectMapper.writeValueAsString(tokenReq))
                .with(headers(content = true, authorised = false)))
                .andExpect(status().isOk)
                .andDo { result -> player2Token = objectMapper.readValue(result.response.contentAsString,
                        ObjectNode::class.java)["token"].asText()}

        val placementsReq = InitialPlacementsRequest(2, arrayOf(
                InitialPlacement("king", 0, 4),
                InitialPlacement("pawn", 1, 4),
                InitialPlacement("pawn", 2, 4),
                InitialPlacement("king", 3, 4),
                InitialPlacement("king", 2, 5)
        ))

        mockMvc.perform(post("/game/{gameId}/setup", gameId).content(objectMapper.writeValueAsString(placementsReq))
                .with(headers(content = true, authorised = true, token = player2Token)))
                .andExpect(status().isOk)

        val confirmReq = ConfirmSetupRequest(2)

        mockMvc.perform(post("/game/{gameId}/setup/confirm", gameId).content(objectMapper.writeValueAsString(confirmReq))
                .with(headers(content = true, authorised = true, token = player2Token)))
                .andExpect(status().isOk)
    }

    private fun doTurnMove() {
        val req = TurnRequest(1, 1, 2, 2, 2)

        mockMvc.perform(post("/game/{gameId}/turn/move", gameId).content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "move", result) }
                .andDo { result -> writeResponseBody("Example Response", "move", result) }
    }

    private fun doTurnPush() {
        val req = TurnRequest(1, 2, 2, 2, 3)

        mockMvc.perform(post("/game/{gameId}/turn/push", gameId).content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "push", result) }
                .andDo { result -> writeResponseBody("Example Response", "push", result) }
    }

    private fun getActiveGames() {
        mockMvc.perform(get("/games/active").param("accountId", writeState.accountIdsByName[ExampleAccountNames.YOU])
                .with(headers(content = false, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestUri("Example Request", "getActiveGames", result) }
                .andDo { result -> writeResponseBody("Example Response", "getActiveGames", result) }
    }

    private fun resign() {
        val req = ResignRequest(1)

        mockMvc.perform(delete("/game/{gameId}", gameId).content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "resign", result) }
                .andDo { result -> writeResponseBody("Example Response", "resign", result) }
    }

    private fun writeRequestUri(title: String, operationId: String, result: MvcResult) {
        write(title, operationId, "request", "GET " + (if (basePath != "/") basePath else "") + result.request.requestURI +
                getQueryString(result))
    }

    private fun writeRequestBody(title: String, operationId: String, result: MvcResult) {
        write(title, operationId, "request",
                String(IOUtils.toByteArray(result.request.inputStream), Charsets.UTF_8))
    }

    private fun writeResponseBody(title: String, operationId: String, result: MvcResult) {
        write(title, operationId, "response", result.response.contentAsString)
    }

    private fun write(title: String, operationId: String, filename: String, `in`: String) {
        val out = String.format(IOUtils.toString(javaClass.getResource("/json-block.adoctmpl"), Charsets.UTF_8),
                title, `in`)

        FileUtils.writeStringToFile(File(examplesDir,
                "$operationId/operation-responses-after-$filename.adoc"), out, Charsets.UTF_8)
    }

    private fun getQueryString(result: MvcResult): String {
        val qry = StringBuilder()
        for ((key, value1) in result.request.parameterMap) {
            for (value in value1) {
                if (qry.isNotEmpty()) {
                    qry.append("&")
                }
                qry.append(key).append("=").append(value)
            }
        }
        return if (qry.length > 0) qry.insert(0, "?").toString() else ""
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
