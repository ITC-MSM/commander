package com.wingedsheep.assay.compile

import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleFace
import com.wingedsheep.assay.corpus.ScryfallJson
import com.wingedsheep.assay.gate.CardResult
import com.wingedsheep.assay.gate.LineResult
import com.wingedsheep.assay.gate.LineVerdict
import com.wingedsheep.assay.gate.Touchstone
import com.wingedsheep.assay.grammar.CardFragment
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.ScryfallMetadata
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.serialization.CardValidator
import com.wingedsheep.sdk.serialization.LintSeverity

/**
 * Scryfall JSON in, a whole [CardDefinition] out — Assay's reading of a card turned into a card the
 * engine can actually be handed.
 *
 * ## What this is for, and what it is not
 *
 * The module's standing rule is that Assay is **not a runtime card loader**: the corpus stays
 * hand-written, and a `cardDef` with a passing scenario test stays the only ground truth. This is
 * the one carved-out exception, and the carve-out is narrow by construction — the output is handed
 * to a **scenario sandbox** (a dev-gated Scenario Builder session, overlaid on the live registry and
 * discarded with the session), never to the corpus, a deck, a tournament or a persisted game. It is
 * the design's own "Custom cards" note made executable: *is this card expressible in canonical Magic
 * templating, and what exactly does it say?* — answered by playing it.
 *
 * ## Fail-closed, for the same reason the differential is
 *
 * A compile happens only where Assay reads the **whole** card. Every one of these must hold, and
 * each failure is a named [CompileDecline] rather than an approximation:
 *
 * - normalization is invertible for the face (else no grammar verdict means anything);
 * - every line is [LineVerdict.ROUND_TRIP] or [LineVerdict.VARIANT] — a single declined line means a
 *   line whose meaning nobody has read, and a card missing one ability is a card that lies;
 * - the lines fold into one card ([CardFragment.merge]);
 * - the printed header parses — a `*` power has no model, so it declines rather than becoming 0;
 * - [CardValidator] passes on the result, which is the same gate a hand-written card goes through.
 *
 * Partial compilation is the one thing that would make this dangerous: a card that silently dropped
 * its second ability would test *green* and mean nothing. There is deliberately no "best effort"
 * mode and no flag to add one.
 *
 * ## Multi-face cards
 *
 * Declined. A DFC/split/adventure card's second face is a `CardFace` or a `backFace`, and the
 * grammar's unit is a line of one face — nothing here knows which slot a second face belongs in.
 * Naming that as a decline keeps it visible; guessing would put a spell face on a creature.
 */
object CardCompiler {

    /** Compile pasted Scryfall (or Scryfall-style) JSON text. */
    fun compile(json: String, touchstone: Touchstone = Touchstone()): CompileResult {
        val card = ScryfallJson.read(json)
            ?: return CompileResult.Declined(
                cardName = null,
                assay = null,
                declines = listOf(
                    CompileDecline(
                        kind = DeclineKind.UNREADABLE_JSON,
                        detail = "not a readable Scryfall card object — expected at least a \"name\", " +
                            "and a layout that carries Oracle text",
                    )
                ),
            )
        return compile(card, touchstone)
    }

    fun compile(card: OracleCard, touchstone: Touchstone = Touchstone()): CompileResult {
        val assay = touchstone.assay(card)
        val declines = mutableListOf<CompileDecline>()

        if (card.faces.size > 1) {
            declines += CompileDecline(
                DeclineKind.MULTI_FACE,
                "a ${card.layout} card has ${card.faces.size} faces; the compiler reads single-faced cards",
            )
            return CompileResult.Declined(card.name, assay, declines)
        }

        val face = card.faces.single()
        val faceResult = assay.faces.single()

        if (!faceResult.normalizationHolds) {
            declines += CompileDecline(
                DeclineKind.NORMALIZATION,
                "normalization is not invertible for this text, so no line verdict on it is trustworthy",
            )
        }
        for (line in faceResult.lines) {
            when (line.verdict) {
                LineVerdict.ROUND_TRIP, LineVerdict.VARIANT -> Unit
                LineVerdict.DECLINED -> declines += line.decline(
                    DeclineKind.LINE_DECLINED,
                    "no rule reads this line" + (line.declineToken?.let { ", from \"$it\"" } ?: ""),
                )

                LineVerdict.AMBIGUOUS -> declines += line.decline(
                    DeclineKind.LINE_AMBIGUOUS,
                    "two rules read this line into two different models",
                )

                LineVerdict.MISMATCH -> declines += line.decline(
                    DeclineKind.LINE_MISMATCH,
                    "the model this line parsed to prints back as something else" +
                        (line.printed?.let { ": \"$it\"" } ?: ""),
                )
            }
        }

        val fragment = faceResult.lines
            .mapNotNull { it.model }
            .fold<CardFragment, CardFragment?>(CardFragment.EMPTY) { acc, next -> acc?.merge(next) }
        if (fragment == null) {
            declines += CompileDecline(
                DeclineKind.LINES_DO_NOT_FOLD,
                "the lines read individually but cannot be one card — two of them claim the same slot",
            )
        }

        val header = readHeader(face, declines)

        if (declines.isNotEmpty() || fragment == null || header == null) {
            return CompileResult.Declined(card.name, assay, declines)
        }

        // Constructing the definition can still fail on an SDK invariant this file does not know
        // about: the model types validate in `init`, and each one of those is a way for the SDK to
        // say "no card looks like this". Every such refusal is a *finding* — the same product a
        // declined line is — so it is caught and named rather than allowed to propagate. Letting one
        // escape would take out whatever is driving the compile: a corpus-wide bake, or the Scenario
        // Builder's paste box, which would 500 instead of showing the author what is wrong.
        val definition = runCatching { definition(card, face, header, fragment) }.getOrElse { e ->
            return CompileResult.Declined(
                card.name,
                assay,
                listOf(
                    CompileDecline(
                        DeclineKind.INVALID_CARD,
                        "the SDK will not construct this card: ${e.message ?: e::class.simpleName}",
                    )
                ),
            )
        }
        // The same validator a hand-written card is loaded through, and the same split: an ERROR is
        // a card the SDK considers malformed, a WARNING is the linter's advice. Only the first is
        // fail-closed — declining on advice would make the compiler stricter than the corpus.
        val validation = CardValidator.validate(definition)
        val errors = validation.filter { it.severity == LintSeverity.ERROR }
        if (errors.isNotEmpty()) {
            return CompileResult.Declined(
                card.name,
                assay,
                errors.map { CompileDecline(DeclineKind.INVALID_CARD, it.message) },
            )
        }
        return CompileResult.Compiled(
            definition = definition,
            assay = assay,
            warnings = validation.filter { it.severity == LintSeverity.WARNING }.map { it.message },
        )
    }

    private fun LineResult.decline(kind: DeclineKind, detail: String) =
        CompileDecline(kind, detail, lineIndex = index, line = line)

    /**
     * The printed characteristics that are not text. Each unreadable one is its own decline, so a
     * card with a `*` power and an unknown line reports both rather than the first one hit.
     */
    private fun readHeader(face: OracleFace, declines: MutableList<CompileDecline>): Header? {
        val typeLine = runCatching { TypeLine.parse(face.typeLine) }.getOrNull()
        if (typeLine == null) {
            declines += CompileDecline(DeclineKind.HEADER, "type line \"${face.typeLine}\" does not parse")
        }
        val manaCost = runCatching { ManaCost.parse(face.manaCost) }.getOrNull()
        if (manaCost == null) {
            declines += CompileDecline(DeclineKind.HEADER, "mana cost \"${face.manaCost}\" does not parse")
        }

        // `*` and `1+*` are characteristic-defining abilities: real printed values with a real SDK
        // model (`CharacteristicValue` is not just `Fixed`), but one nothing here has read — the
        // defining ability is a *line*, and mapping it to the stat slot is grammar work that has not
        // been done. Reading it as 0 would be the exact "reversible but wrong" failure the module
        // exists to prevent, so it declines and says which value it could not read.
        //
        // A *negative* printed value parses as a number and is still unrepresentable: `CreatureStats`
        // requires a non-negative base (Spinal Parasite's -1/-1, and the Un-sets). That is an SDK
        // finding rather than a grammar gap, and it is reported the same way every other one is —
        // as a decline naming the value. Note it must be caught here rather than left to the
        // `CreatureStats` constructor, which throws: a compiler that crashed on a card would break
        // "declining is success" for every caller, and the corpus sweep that found this was the
        // first thing to hand it a card printed that way.
        val power = face.power?.toIntOrNull()?.takeIf { it >= 0 }
        val toughness = face.toughness?.toIntOrNull()?.takeIf { it >= 0 }
        if (typeLine?.isCreature == true && (power == null || toughness == null)) {
            declines += CompileDecline(
                DeclineKind.HEADER,
                "power/toughness \"${face.power}/${face.toughness}\" is not a fixed non-negative number",
            )
        }
        val loyalty = face.loyalty?.toIntOrNull()
        val defense = face.defense?.toIntOrNull()

        if (typeLine == null || manaCost == null) return null
        return Header(typeLine, manaCost, power, toughness, loyalty, defense)
    }

    /**
     * Build the definition.
     *
     * It does **not** go through the `cardDef { }` DSL, and that is the one place this file departs
     * from the module's "build through the SDK's own facades" rule. The DSL assembles a `CardScript`
     * from nested builder calls; Assay already holds a finished `CardScript`, and there is no seam to
     * hand one in. Widening the DSL for a single non-card caller would put a back door into the
     * surface every real card is authored through — worse than the two derivations replicated here,
     * which are named individually below.
     */
    private fun definition(
        card: OracleCard,
        face: OracleFace,
        header: Header,
        fragment: CardFragment,
    ): CardDefinition = CardDefinition(
        name = card.name,
        manaCost = header.manaCost,
        typeLine = header.typeLine,
        oracleText = face.oracleText,
        creatureStats = if (header.power != null && header.toughness != null) {
            CreatureStats(header.power, header.toughness)
        } else {
            null
        },
        // Derivation 1 (`CardBuilder.build`): a parameterized keyword ability also announces itself
        // as a bare `Keyword`, and half the engine reads that set rather than the ability list.
        keywords = fragment.keywordAbilities.mapNotNull { it.keyword }.toSet(),
        flags = fragment.flags,
        keywordAbilities = fragment.keywordAbilities,
        script = withDistinctAbilityIds(fragment.script),
        // Derivation 3 (`CardBuilder.equipAbility`): "Equip {1}" is one line filling two slots, and
        // the field half is not decoration — `CardValidator` requires an Equipment type line
        // wherever it is set, and the engine's equip permissions read it.
        equipCost = fragment.equipCost,
        oracleId = card.oracleId,
        setCode = card.setCode,
        metadata = ScryfallMetadata(imageUri = face.imageUri),
        startingLoyalty = header.loyalty,
        startingDefense = header.defense,
        // Derivation 2 (`CardBuilder.build`): a blank mana cost means "can't be cast normally"
        // (CR 202.1b/118.6) — but never for a land, which is played rather than cast (CR 305).
        hasNoManaCost = face.manaCost.isBlank() && !header.typeLine.isLand,
    )

    /**
     * Re-mint every ability id.
     *
     * The grammar mints one fixed constant per family (`Triggers.ID`, `Activated.ID`) on purpose: no
     * printed word determines an id, so reproducing a generated one would be reading a counter
     * rather than a card, and the differential normalizes both sides by position. A *played* card
     * cannot live with that — `ActivatedAbility`s are dispatched by
     * `activatedAbilities.find { it.id == action.abilityId }` and activation limits are tracked per
     * id, so two abilities sharing one would activate the wrong ability and share one once-per-turn
     * counter. Nothing Assay produces points *at* an id, which is what makes re-minting safe here
     * and wrong in the grammar.
     */
    private fun withDistinctAbilityIds(script: CardScript): CardScript = script.copy(
        triggeredAbilities = script.triggeredAbilities.map { it.copy(id = AbilityId.generate()) },
        activatedAbilities = script.activatedAbilities.map { it.copy(id = AbilityId.generate()) },
    )

    private data class Header(
        val typeLine: TypeLine,
        val manaCost: ManaCost,
        val power: Int?,
        val toughness: Int?,
        val loyalty: Int?,
        val defense: Int?,
    )
}

sealed interface CompileResult {

    /** The reading Assay produced, for display next to the outcome. Null only for unreadable JSON. */
    val assay: CardResult?

    data class Compiled(
        val definition: CardDefinition,
        override val assay: CardResult,
        /** Linter advice — reported beside the card, never a reason to refuse it. */
        val warnings: List<String> = emptyList(),
    ) : CompileResult

    data class Declined(
        val cardName: String?,
        override val assay: CardResult?,
        val declines: List<CompileDecline>,
    ) : CompileResult
}

enum class DeclineKind {
    UNREADABLE_JSON,
    MULTI_FACE,
    NORMALIZATION,
    LINE_DECLINED,
    LINE_AMBIGUOUS,
    LINE_MISMATCH,
    LINES_DO_NOT_FOLD,
    HEADER,
    INVALID_CARD,
}

/** One reason a card was not compiled, tied to the line that caused it where there is one. */
data class CompileDecline(
    val kind: DeclineKind,
    val detail: String,
    val lineIndex: Int? = null,
    val line: String? = null,
)
