package me.hdpe.pushfight.server.web.swagger

import com.fasterxml.classmate.TypeResolver
import me.hdpe.pushfight.engine.Piece
import me.hdpe.pushfight.engine.Player
import me.hdpe.pushfight.engine.Square
import org.springframework.stereotype.Component
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.ModelProperty
import springfox.documentation.schema.ResolvedTypes
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelBuilderPlugin
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

/**
 * We need to make some manual changes to the Swagger model to bring it in line with the customisations in the
 * Jackson module.
 */
@Component
class ModelEnhancingSwaggerPlugin(val typeResolver: TypeResolver, val typeNameExtractor: TypeNameExtractor) :
        ModelBuilderPlugin, ModelPropertyBuilderPlugin {

    override fun apply(context: ModelContext) {
        if (context.type == typeResolver.resolve(Player::class.java)) {
            context.builder.properties(mapOf(
                    Pair("accountId", toModelProperty(ModelPropertyBuilder()
                            .name("accountId")
                            .required(true)
                            .type(typeResolver.resolve(java.lang.String::class.java)), context)
                    ),
                    Pair("playerName", toModelProperty(ModelPropertyBuilder()
                            .name("playerName")
                            .required(true)
                            .type(typeResolver.resolve(java.lang.String::class.java)), context)
                    )
            ))
        }

        if (context.type == typeResolver.resolve(Piece::class.java)) {
            context.builder.properties(mapOf(
                    Pair("type", toModelProperty(ModelPropertyBuilder()
                            .name("type")
                            .required(true)
                            .allowableValues(AllowableListValues(listOf("king", "pawn"), null))
                            .type(typeResolver.resolve(java.lang.String::class.java)), context)
                    ),
                    Pair("hatted", toModelProperty(ModelPropertyBuilder()
                            .name("hatted")
                            .type(typeResolver.resolve(java.lang.Boolean::class.java)), context)
                    )
            ))
        }

        if (context.type == typeResolver.resolve(Square::class.java)) {
            context.builder.properties(mapOf(
                    Pair("type", toModelProperty(ModelPropertyBuilder()
                            .name("type")
                            .required(true)
                            .allowableValues(AllowableListValues(listOf("abyss", "board"), null))
                            .type(typeResolver.resolve(java.lang.String::class.java)), context)
                    ),
                    Pair("rail", toModelProperty(ModelPropertyBuilder()
                            .name("rail")
                            .allowableValues(AllowableListValues(listOf("left", "right"), null))
                            .type(typeResolver.resolve(java.lang.String::class.java)), context)
                    ),
                    Pair("piece", toModelProperty(ModelPropertyBuilder()
                            .name("piece")
                            .type(typeResolver.resolve(Piece::class.java)), context))
            ))
        }
    }

    override fun apply(context: ModelPropertyContext) {
        val propertyName = context.beanPropertyDefinition?.get()?.fullName?.simpleName

        if (propertyName == "owner") {
            context.builder.type(typeResolver.resolve(java.lang.Integer::class.java))
                    .allowableValues(AllowableRangeValues("1", "2"))
        }
    }

    override fun supports(delimiter: DocumentationType): Boolean = (delimiter == DocumentationType.SWAGGER_2)

    private fun toModelProperty(builder: ModelPropertyBuilder, ctx: ModelContext): ModelProperty {
        val property = builder.build()
        property.updateModelRef(ResolvedTypes.modelRefFactory(ctx, typeNameExtractor))
        return property
    }
}