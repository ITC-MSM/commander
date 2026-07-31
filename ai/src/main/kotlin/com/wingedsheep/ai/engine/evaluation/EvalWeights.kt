package com.wingedsheep.ai.engine.evaluation

import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.ai.engine.knowledge.IntentCatalog
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The five-feature fallback evaluator's coefficients.
 *
 * Phase 9 will add a raw-feature evaluator; keeping this vector loadable makes the existing
 * evaluator a safe fallback and gives the arena a resource-backed A/B seam in the meantime.
 */
@Serializable
data class EvaluationWeights(
    val life: Double = 1.0,
    val boardPresence: Double = 1.5,
    val cardAdvantage: Double = 1.0,
    val threatAssessment: Double = 1.2,
    val tempo: Double = 0.6,
) {
    fun toEvaluator(intents: IntentCatalog = IntentCatalog.NONE): BoardEvaluator = CompositeBoardEvaluator(
        listOf(
            life to LifeDifferential,
            boardPresence to BoardFeature { state, projected, playerId ->
                BoardPresence.score(state, projected, playerId, intents)
            },
            cardAdvantage to CardAdvantage,
            threatAssessment to ThreatAssessment,
            tempo to Tempo,
        )
    )

    companion object {
        /** Compiled fallback: resource loading can never make the production AI unavailable. */
        val DEFAULT = EvaluationWeights()
        val BLIND = EvaluationWeights(0.0, 0.0, 0.0, 0.0, 0.0)
    }
}

/**
 * Resource-backed evaluation vectors, keyed by the stable id carried by [AiProfile].
 *
 * A bad tuning artifact deliberately fails closed to [EvaluationWeights.DEFAULT]. Evaluation
 * tuning is an optimization, not a reason for a game server to fail during startup.
 */
object EvalWeights {
    const val DEFAULT_ID = "default"
    private const val RESOURCE = "ai/eval-weights.json"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = false
    }

    private val resourceWeights: Map<String, EvaluationWeights> by lazy {
        runCatching {
            val text = EvalWeights::class.java.classLoader
                .getResourceAsStream(RESOURCE)
                ?.bufferedReader()
                ?.use { it.readText() }
                ?: return@runCatching emptyMap()
            decodeOrEmpty(text)
        }.getOrDefault(emptyMap())
    }

    fun resolve(id: String): EvaluationWeights =
        resourceWeights[id]?.takeIf(::isFinite) ?: EvaluationWeights.DEFAULT

    /** Stable ids available to tooling such as the arena agent registry. */
    val ids: Set<String> get() = resourceWeights.keys

    internal fun decode(text: String): Map<String, EvaluationWeights> =
        json.decodeFromString<Map<String, EvaluationWeights>>(text)

    internal fun decodeOrEmpty(text: String): Map<String, EvaluationWeights> =
        runCatching { decode(text) }.getOrDefault(emptyMap())

    private fun isFinite(weights: EvaluationWeights): Boolean =
        weights.life.isFinite() &&
            weights.boardPresence.isFinite() &&
            weights.cardAdvantage.isFinite() &&
            weights.threatAssessment.isFinite() &&
            weights.tempo.isFinite()
}
