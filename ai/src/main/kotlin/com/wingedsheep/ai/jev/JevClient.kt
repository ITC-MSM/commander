package com.wingedsheep.ai.jev

import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/** Jev uses the Decisions API, not chat completions. Keys are local opaque choice IDs. */
fun interface JevChoiceClient {
    fun choose(state: String, instructions: String, criteria: Map<String, String>, timeoutMs: Long): String
}

data class JevConfig(
    val apiKey: String = "",
    val endpoint: String = "https://openrouter.ai/api/alpha/decisions",
    val model: String = "typesafe/jev-1.13",
    val timeoutMs: Long = 30_000,
)

class JevClient(private val config: JevConfig) : JevChoiceClient {
    private val http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun choose(state: String, instructions: String, criteria: Map<String, String>, timeoutMs: Long): String {
        require(criteria.size in 2..255)
        require(config.apiKey.isNotBlank()) { "Jev requires GAME_AI_JEV_API_KEY or OPENROUTER_API_KEY" }
        val body = buildJsonObject {
            put("model", config.model)
            put("state", state)
            putJsonObject("questions") {
                putJsonObject("move") {
                    put("type", "choice")
                    put("instructions", instructions)
                    put("criteria", JsonObject(criteria.mapValues { JsonPrimitive(it.value) }))
                }
            }
        }.toString()
        // Conservative guard under Jev's 32K context, including escaped JSON and question text.
        require(body.toByteArray(Charsets.UTF_8).size <= 28_000) { "Jev request exceeds the context budget" }
        val request = HttpRequest.newBuilder(URI.create(config.endpoint))
            .header("Authorization", "Bearer ${config.apiKey}")
            .header("Content-Type", "application/json")
            .timeout(Duration.ofMillis(timeoutMs.coerceAtMost(config.timeoutMs)))
            .POST(HttpRequest.BodyPublishers.ofString(body)).build()
        val response = http.send(request, HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() in 200..299) { "Jev HTTP ${response.statusCode()}" }
        val result = Json.parseToJsonElement(response.body()).jsonObject
        val answer = result["answers"]?.jsonObject?.get("move")?.jsonObject
        check(answer?.get("type")?.jsonPrimitive?.content == "choice") { "Missing Jev choice answer" }
        val choice = answer["choice"]?.jsonPrimitive?.content
        check(choice in criteria) { "Jev returned an unknown choice" }
        logger.info("Jev decision: model={}, choice={}, cost={}", config.model, choice,
            result["usage"]?.jsonObject?.get("cost"))
        return choice!!
    }
}
