package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.ActionResponse
import com.wingedsheep.ai.AiPlayerController
import com.wingedsheep.ai.engine.EngineAiPlayerController
import com.wingedsheep.ai.jev.JevAiPlayerController
import com.wingedsheep.ai.jev.JevClient
import com.wingedsheep.ai.jev.JevConfig
import com.wingedsheep.engine.core.ActionProcessor
import com.wingedsheep.engine.core.SubmitDecision
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.gameserver.config.GameProperties
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/** Opt-in local development opponent. Credentials and unmasked state never leave the server. */
@Component
@Profile("local")
class JevControllerProvider(
    private val registry: CardRegistry,
    private val properties: GameProperties,
    @Value("\${game.ai.jev.endpoint:https://openrouter.ai/api/alpha/decisions}") private val endpoint: String,
    @Value("\${game.ai.jev.model:typesafe/jev-1.13}") private val model: String,
    @Value("\${game.ai.jev.timeout-ms:30000}") private val timeoutMs: Long,
) : AiControllerProvider {
    override val mode = "jev"
    private val apiKey: String get() = properties.ai.openRouterApiKey

    @PostConstruct
    fun validateConfig() {
        if (properties.ai.enabled && properties.ai.mode.trim().equals(mode, ignoreCase = true)) {
            require(apiKey.isNotBlank()) { "Jev requires OPENROUTER_API_KEY" }
            require(timeoutMs in 1..120_000) { "Jev timeout must be between 1 and 120000 ms" }
        }
    }

    override fun create(context: AiControllerContext): AiPlayerController {
        val processor = ActionProcessor(registry)
        val viewTransformer = ClientStateTransformer(registry)
        val fallback = EngineAiPlayerController(registry, context.playerId, { context.snapshot()?.state })
        return JevAiPlayerController(context.playerId, JevClient(JevConfig(apiKey, endpoint, model, timeoutMs)), fallback,
            validate = { response ->
                val state = context.snapshot()?.state
                if (state == null) "No live game state" else {
                    val action = when (response) {
                        is ActionResponse.SubmitAction -> response.action
                        is ActionResponse.SubmitDecision -> SubmitDecision(response.playerId, response.response)
                    }
                    // Pure preview. The session remains the sole owner of actual state mutation/events.
                    processor.process(state, action).result.error
                }
            }, timeoutMs = timeoutMs,
            maskedStateProvider = {
                // Local debug mode reveals both hands to the UI. Never reuse that transport view.
                viewTransformer.transform(requireNotNull(context.snapshot()).state, context.playerId)
            })
    }
}
