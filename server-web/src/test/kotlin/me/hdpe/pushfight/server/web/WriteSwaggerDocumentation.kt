package me.hdpe.pushfight.server.web

import io.github.swagger2markup.GroupBy
import io.github.swagger2markup.Swagger2MarkupConverter
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.io.File
import java.nio.file.Paths
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [
    WebConfig::class,
    WriteConfig::class
])
@TestPropertySource("classpath:/test-application.properties")
class WriteSwaggerDocumentation {

    @Value("#{systemProperties['examples.dir']?:'target/examples'}")
    private lateinit var examplesDir: File

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @Configuration
    @EnableAutoConfiguration
    class WriteSwaggerDocumentationConfig {
    }

    @BeforeEach
    fun setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build()
    }

    @Test
    fun convertSwaggerToAsciiDoc() {
        this.mockMvc.perform(get("/v2/api-docs")
                .accept(MediaType.APPLICATION_JSON))
                .andDo { result -> writeAsciiDoc(result.response.contentAsString) }
                .andExpect(status().isOk())
    }

    private fun writeAsciiDoc(swaggerJson: String) {
        Swagger2MarkupConverter.from(swaggerJson)
                .withConfig(Swagger2MarkupConfigBuilder(
                        hashMapOf(
                                Pair("swagger2markup.extensions.dynamicPaths.contentPath", examplesDir.absolutePath),
                                Pair("swagger2markup.extensions.dynamicOverview.contentPath", "src/test/resources/asciidoc/overview")
                        )
                )
                .withTagOrdering(byLogical())
                .withPathsGroupedBy(GroupBy.TAGS)
                .build())
            .build()
            .toFolder(Paths.get("target/asciidoc/generated"))
    }

    private fun byLogical(): Comparator<String> {
        val reverseTargetOrder = arrayOf("Game", "Accounts", "Authentication")

        val stringComparator = Comparator.comparingInt<String> { reverseTargetOrder.indexOf(it) }
        return stringComparator.reversed()
    }
}
