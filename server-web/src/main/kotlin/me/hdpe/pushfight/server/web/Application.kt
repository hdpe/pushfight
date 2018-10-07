package me.hdpe.pushfight.server.web

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(WebConfig::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}