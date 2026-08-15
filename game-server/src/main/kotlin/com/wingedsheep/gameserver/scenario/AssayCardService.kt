package com.wingedsheep.gameserver.scenario

import com.wingedsheep.assay.compile.CardCompiler
import com.wingedsheep.assay.compile.CompileResult
import com.wingedsheep.assay.gate.LineVerdict
import com.wingedsheep.assay.syntax.explain
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.serialization.CardSerialization
import kotlinx.serialization.json.JsonElement
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * The Scenario Builder's custom-card sandbox: Scryfall(-style) JSON in, a playable
 * [CardDefinition] out, via Argentum Assay's grammar.
 *
 * ## Why this can exist without making Assay a card loader
 *
 * `:oracle-assay` is an auditor, and its standing rule is that a hand-written `cardDef` with a
 * passing scenario test is the only ground truth for the corpus. Three properties keep this inside
 * that rule, and all three are enforced here rather than by convention:
 *
 * 1. **Dev-gated.** [enabled] mirrors `game.dev-endpoints.enabled`, the same flag that switches on
 *    `DevScenarioController`. With it off, a request carrying custom cards is rejected — including
 *    on the *player-facing* `/api/scenarios`, which shares this service precisely so there is one
 *    gate rather than one per controller.
 * 2. **Session-scoped.** A compiled card is registered into a [CardRegistry] overlay built for one
 *    scenario ([resolve]), never into the live corpus. When the session is reaped the card is gone;
 *    it cannot be drafted, deck-built, persisted, or seen by anyone in another game.
 * 3. **Whole cards only.** [CardCompiler] refuses to compile a card any of whose lines Assay could
 *    not read, so nothing here can produce a card that silently lost an ability. A card that will
 *    not compile comes back as a list of reasons pointing at the line that caused each one — which
 *    is the actual product, and the reason this is worth having: it answers "is this card
 *    expressible in Argentum's vocabulary, and what exactly does it say?" by letting you play it.
 */
@Service
class AssayCardService(
    private val cardRegistry: CardRegistry,
    @Value("\${game.dev-endpoints.enabled:false}") private val enabled: Boolean,
) {

    /**
     * The registry a scenario should be built and played against.
     *
     * With no custom cards this is the live registry itself — no allocation, no behaviour change on
     * the path every ordinary scenario takes. With custom cards it is a child overlay: lookups find
     * the pasted card first and fall through to the corpus for everything else, and registering into
     * a child never mutates the parent.
     */
    fun resolve(request: ScenarioRequest): Resolution {
        val sources = request.customCards.orEmpty()
        if (sources.isEmpty()) return Resolution(cardRegistry, emptyList(), emptyList())
        if (!enabled) {
            return Resolution(
                cardRegistry,
                emptyList(),
                listOf("Custom cards are only available when dev endpoints are enabled on this server."),
            )
        }
        if (sources.size > MAX_CUSTOM_CARDS) {
            return Resolution(
                cardRegistry,
                emptyList(),
                listOf("Too many custom cards (${sources.size}); max $MAX_CUSTOM_CARDS."),
            )
        }

        val overlay = CardRegistry(parent = cardRegistry)
        val compiled = mutableListOf<CardDefinition>()
        val errors = mutableListOf<String>()
        for (source in sources) {
            if (source.length > MAX_CARD_JSON_BYTES) {
                errors += "A custom card's JSON is too large (${source.length} chars); max $MAX_CARD_JSON_BYTES."
                continue
            }
            when (val result = CardCompiler.compile(source)) {
                is CompileResult.Compiled -> {
                    overlay.register(result.definition)
                    compiled += result.definition
                }

                is CompileResult.Declined -> {
                    val name = result.cardName ?: "custom card"
                    errors += result.declines.map { decline ->
                        val where = decline.line?.let { " — \"$it\"" } ?: ""
                        "$name did not compile: ${decline.detail}$where"
                    }
                }
            }
        }
        return Resolution(overlay, compiled, errors)
    }

    /** Compile one pasted card for inspection — the builder's "check this card" button. */
    fun inspect(json: String): AssayCompileResponse {
        if (json.length > MAX_CARD_JSON_BYTES) {
            return AssayCompileResponse(
                cardName = null,
                compiled = false,
                declines = listOf(
                    AssayDecline("SIZE", "JSON is too large (${json.length} chars); max $MAX_CARD_JSON_BYTES."),
                ),
            )
        }
        val result = CardCompiler.compile(json)
        val lines = result.assay?.faces?.flatMap { face ->
            face.lines.map { line ->
                AssayLineReading(
                    index = line.index,
                    text = line.line,
                    verdict = line.verdict.name,
                    // A VARIANT prints back in canonical templating; showing what it became is the
                    // whole feedback loop for an author writing a custom card's text.
                    printed = line.printed.takeIf { line.verdict == LineVerdict.VARIANT },
                    explanation = line.decline?.explain(line.line),
                )
            }
        }.orEmpty()

        return when (result) {
            is CompileResult.Compiled -> AssayCompileResponse(
                cardName = result.definition.name,
                compiled = true,
                lines = lines,
                warnings = result.warnings,
                definition = CardSerialization.json
                    .encodeToJsonElement(CardDefinition.serializer(), result.definition),
            )

            is CompileResult.Declined -> AssayCompileResponse(
                cardName = result.cardName,
                compiled = false,
                lines = lines,
                declines = result.declines.map {
                    AssayDecline(it.kind.name, it.detail, it.lineIndex, it.line)
                },
            )
        }
    }

    /** Whether the sandbox is switched on, so the client can hide the panel instead of guessing. */
    val isEnabled: Boolean get() = enabled

    data class Resolution(
        /** The registry to build and play the scenario against — the overlay, or the live one. */
        val registry: CardRegistry,
        val cards: List<CardDefinition>,
        val errors: List<String>,
    )

    companion object {
        /** A scenario's custom-card cap. Generous for testing, bounded against a paste bomb. */
        const val MAX_CUSTOM_CARDS = 20

        /** Scryfall's largest real card object is a few KB; this leaves room and nothing more. */
        const val MAX_CARD_JSON_BYTES = 20_000
    }
}

/** Body for the dev compile endpoint: one Scryfall(-style) card object, as pasted. */
data class AssayCompileRequest(val json: String)

/**
 * One printed line as Assay read it. [verdict] is the touchstone's own vocabulary
 * (`ROUND_TRIP` / `VARIANT` / `DECLINED` / `AMBIGUOUS` / `MISMATCH`) rather than a server-side
 * re-classification, so the builder shows what the gates show.
 */
data class AssayLineReading(
    val index: Int,
    val text: String,
    val verdict: String,
    /** The canonical spelling, when the line was written a different legal way. */
    val printed: String? = null,
    /** `assay explain`'s caret, pointing at the token the parse died on. */
    val explanation: String? = null,
)

data class AssayDecline(
    val kind: String,
    val detail: String,
    val lineIndex: Int? = null,
    val line: String? = null,
)

data class AssayCompileResponse(
    val cardName: String?,
    val compiled: Boolean,
    val lines: List<AssayLineReading> = emptyList(),
    val declines: List<AssayDecline> = emptyList(),
    val warnings: List<String> = emptyList(),
    /** The compiled card, for eyeballing what the text actually became. Null when it declined. */
    val definition: JsonElement? = null,
)
