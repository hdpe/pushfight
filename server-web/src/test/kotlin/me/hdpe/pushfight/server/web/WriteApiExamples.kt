package me.hdpe.pushfight.server.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.base.Charsets
import me.hdpe.pushfight.engine.command.PieceType
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

    @Value("#{systemProperties['examples.dir']}")
    private lateinit var examplesDir: File

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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

        accounts()

        createGame()

        initialPlacements()
        updatePlacements()
        confirmSetup()
    }

    private fun token() {
        val req = TokenRequest("testAccessKeyId", "s3CrEt")

        mockMvc.perform(post("/token").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = false)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "token", result) }
                .andDo { result -> writeResponseBody("Example Response", "token", result) }
                .andDo { result -> token = objectMapper.readValue(result.response.contentAsString,
                        ObjectNode::class.java)["token"].asText()}
    }

    private fun accounts() {
        mockMvc.perform(get("/accounts")
                .with(headers(content = false, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestUri("Example Request", "accounts", result) }
                .andDo { result -> writeResponseBody("Example Response", "accounts", result) }
    }

    private fun createGame() {
        val req = CreateGameRequest("1001")

        mockMvc.perform(post("/game").content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "createGame", result) }
                .andDo { result -> writeResponseBody("Example Response", "createGame", result) }
                .andDo { result -> gameId = objectMapper.readValue(result.response.contentAsString,
                        ObjectNode::class.java)["id"].asText()}
    }

    private fun initialPlacements() {
        val req = InitialPlacementsRequest(1, arrayOf(
                InitialPlacement(PieceType.KING, 0, 3),
                InitialPlacement(PieceType.PAWN, 1, 3),
                InitialPlacement(PieceType.PAWN, 2, 3),
                InitialPlacement(PieceType.KING, 3, 3),
                InitialPlacement(PieceType.KING, 2, 4)
        ))

        mockMvc.perform(post("/game/{gameId}/setup", gameId).content(objectMapper.writeValueAsString(req))
                .with(headers(content = true, authorised = true)))
                .andExpect(status().isOk)
                .andDo { result -> writeRequestBody("Example Request", "initialPlacements", result) }
                .andDo { result -> writeResponseBody("Example Response", "initialPlacements", result) }
    }

    private fun updatePlacements() {
        val req = UpdatePlacementsRequest(1, arrayOf(UpdatedPlacement(2, 4, 1, 4)))

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

    private fun writeRequestUri(title: String, operationId: String, result: MvcResult) {
        write(title, operationId, "request", "GET " + basePath + result.request.requestURI +
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
        val qry = StringBuilder("?")
        for ((key, value1) in result.request.parameterMap) {
            for (value in value1) {
                if (qry.isNotEmpty()) {
                    qry.append("&")
                }
                qry.append(key).append("=").append(value)
            }
        }
        return qry.toString()
    }

    private fun headers(content: Boolean = false, authorised: Boolean = false): RequestPostProcessor = RequestPostProcessor {
        if (authorised) {
            it.addHeader("Authorization", "Bearer $token")
        }
        it.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        if (content) {
            it.addHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        }
        return@RequestPostProcessor it
    }
}
