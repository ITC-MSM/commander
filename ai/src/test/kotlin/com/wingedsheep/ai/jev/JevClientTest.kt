package com.wingedsheep.ai.jev

import com.sun.net.httpserver.HttpServer
import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.*
import java.net.InetSocketAddress

class JevClientTest : FunSpec({
    test("uses Decisions API choice schema and bearer auth, accepts typed answer") {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        var request: JsonObject? = null
        var authorization: String? = null
        server.createContext("/alpha/decisions") { exchange ->
            authorization = exchange.requestHeaders.getFirst("Authorization")
            request = Json.parseToJsonElement(exchange.requestBody.bufferedReader().readText()).jsonObject
            val body = """{"answers":{"move":{"type":"choice","choice":"c1","confidence":0.8}},"usage":{"cost":0.001}}""".toByteArray()
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        server.start()
        try {
            val client = JevClient(JevConfig("test-key", "http://127.0.0.1:${server.address.port}/alpha/decisions"))
            client.choose("masked state", "Pick", mapOf("c0" to "Pass", "c1" to "Attack"), 1000) shouldBe "c1"
            authorization shouldBe "Bearer test-key"
            request!!["model"]!!.jsonPrimitive.content shouldBe "typesafe/jev-1.13"
            request!!["state"]!!.jsonPrimitive.content shouldBe "masked state"
            request!!["questions"]!!.jsonObject["move"]!!.jsonObject["type"]!!.jsonPrimitive.content shouldBe "choice"
            request!!.containsKey("messages") shouldBe false
        } finally { server.stop(0) }
    }

    test("rejects HTTP errors, malformed answers and choices not in the request") {
        for ((status, response) in listOf(429 to "{}", 200 to "not json",
            200 to """{"answers":{"move":{"type":"choice","choice":"invented"}}}""")) {
            val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
            server.createContext("/") { exchange ->
                exchange.requestBody.close()
                val bytes = response.toByteArray()
                exchange.sendResponseHeaders(status, bytes.size.toLong())
                exchange.responseBody.use { it.write(bytes) }
            }
            server.start()
            try {
                shouldThrowAny {
                    JevClient(JevConfig("test", "http://127.0.0.1:${server.address.port}/"))
                        .choose("state", "choose", mapOf("c0" to "A", "c1" to "B"), 1000)
                }
            } finally { server.stop(0) }
        }
    }

    test("choice sessions reject invented IDs and skip forced choices") {
        val q = JevChoices(JevChoiceClient { _, _, _, _ -> "invented" }, "state", 1000)
        q.pick("forced", listOf(9)) shouldBe 9
        shouldThrowAny { q.pick("choose", listOf(1, 2)) }
    }

    test("large choices and integer ranges preserve the last option beyond cardinality 255") {
        val q = JevChoices(JevChoiceClient { _, _, choices, _ ->
            check(choices.size <= 255)
            choices.keys.last()
        }, "state", 1000)
        q.pick("choose", (0..600).toList()) shouldBe 600
        q.number("X", 0, Int.MAX_VALUE) shouldBe Int.MAX_VALUE
    }
})
