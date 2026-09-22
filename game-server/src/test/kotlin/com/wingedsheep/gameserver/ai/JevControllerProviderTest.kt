package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.jev.JevAiPlayerController
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gameserver.config.AiProperties
import com.wingedsheep.gameserver.config.GameProperties
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Profile

class JevControllerProviderTest : FunSpec({
    fun provider(key: String = "", mode: String = "jev", timeout: Long = 30000) = JevControllerProvider(
        CardRegistry(), GameProperties(ai = AiProperties(enabled = true, mode = mode, openRouterApiKey = key)),
        "http://127.0.0.1:1/alpha/decisions", "typesafe/jev-1.13", timeout)

    test("Jev is a local-only provider and fails startup with an actionable missing-key error") {
        JevControllerProvider::class.java.getAnnotation(Profile::class.java).value.toList() shouldBe listOf("local")
        shouldThrow<IllegalArgumentException> { provider().validateConfig() }.message shouldBe
            "Jev requires OPENROUTER_API_KEY"
        provider(mode = "engine").validateConfig()
    }
    test("configured provider creates Jev through the same registry as other AI modes") {
        val provider = provider("test")
        provider.validateConfig()
        val registry = AiControllerProviderRegistry(listOf(provider))
        val controller = registry["JEV"]!!.create(AiControllerContext(EntityId("ai"), null) { null })
        (controller is JevAiPlayerController) shouldBe true
    }
    test("invalid decision budget fails before starting a game") {
        shouldThrow<IllegalArgumentException> { provider("test", timeout = 0).validateConfig() }
    }
})
