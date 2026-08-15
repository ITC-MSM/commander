package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The steps a spell performs — the start of the pipeline family, and the first rules that produce
 * something other than a keyword.
 *
 * Each rule targets `mtg-sdk` through its companion facade ([Effects]) rather than a raw
 * constructor, matching the discipline `FacadeBoundaryTest` enforces for cards. A rule's `match`
 * half necessarily destructures the concrete effect class, since that is the only way to read a
 * model back; the asymmetry is inherent to a bidirectional rule and is why `build` going through the
 * facade matters — it is the half that would otherwise drift from how cards are written.
 *
 * Templates are written in their **mid-sentence** form ("draw a card.") because
 * [com.wingedsheep.assay.syntax.SentenceCase] decapitalizes a line before the grammar sees it — the
 * same reason the keyword rules spell themselves "flying" rather than "Flying".
 *
 * ## Singular and plural are separate rules
 *
 * "Draw a card." and "Draw two cards." differ in the article *and* the noun, so one template cannot
 * spell both. They are therefore two rules over disjoint counts — [Cardinals.word] starts at two and
 * the singular rule is the only one that builds 1 — which keeps exactly one printed form per model
 * and leaves nothing for the printer to choose. Overlapping them would be an ambiguity hard error on
 * every draw card in the corpus, which is the grammar telling the truth about a bad factoring.
 */
object Steps {

    // ---------------------------------------------------------------------------------------
    // Draw
    // ---------------------------------------------------------------------------------------

    private val drawOne: Phrase<CardScript> = phrase("draw a card.", name = "draw a card") {
        build { CardScript(spellEffect = Effects.DrawCards(1)) }
        match { script -> if (drawnByController(script) == 1) bind() else null }
    }

    private val drawMany: Phrase<CardScript> = phrase("draw {n} cards.", name = "draw cards") {
        slot("n", Cardinals.word)
        build { CardScript(spellEffect = Effects.DrawCards(it.int("n"))) }
        match { script ->
            val count = drawnByController(script) ?: return@match null
            // The singular is drawOne's to print, and anything Cardinals cannot spell as a word has
            // no surface form here at all. Refusing both is what keeps printing total-or-null
            // rather than total-or-wrong.
            if (count >= 2 && Cardinals.spellable(count)) bind("n" to count) else null
        }
    }

    private val targetPlayerDrawsOne: Phrase<CardScript> =
        phrase("target player draws a card.", name = "target player draws a card") {
            build { targetPlayerDraws(1) }
            match { script -> if (drawnByTarget(script) == 1) bind() else null }
        }

    private val targetPlayerDrawsMany: Phrase<CardScript> =
        phrase("target player draws {n} cards.", name = "target player draws cards") {
            slot("n", Cardinals.word)
            build { targetPlayerDraws(it.int("n")) }
            match { script ->
                val count = drawnByTarget(script) ?: return@match null
                if (count >= 2 && Cardinals.spellable(count)) bind("n" to count) else null
            }
        }

    /** Every step rule, as one alternation. Grows with the family. */
    val all: List<Phrase<CardScript>> = listOf(drawOne, drawMany, targetPlayerDrawsOne, targetPlayerDrawsMany)

    val step: Phrase<CardScript> = oneOf("a spell effect", all)

    // ---------------------------------------------------------------------------------------
    // Model helpers — the `match` side, kept out of the rules so the two draw pairs read alike
    // ---------------------------------------------------------------------------------------

    private fun targetPlayerDraws(count: Int) = CardScript(
        spellEffect = Effects.DrawCards(count, Targets.bound()),
        targetRequirements = listOf(Targets.player()),
    )

    /** The count on a bare "the caster draws" script, or null when the script is anything else. */
    private fun drawnByController(script: CardScript): Int? =
        drawCount(script, requireTarget = false)?.takeIf { script.targetRequirements.isEmpty() }

    /** …and on a "target player draws" script, which must carry the matching requirement. */
    private fun drawnByTarget(script: CardScript): Int? =
        drawCount(script, requireTarget = true)
            ?.takeIf { script.targetRequirements == listOf(Targets.player()) }

    /**
     * Reads the fixed count off a script whose only content is a draw.
     *
     * The `spellEffect`-and-nothing-else check is deliberate and load-bearing: a rule that printed a
     * script it had only partly inspected would drop whatever else was in it and still round-trip,
     * which is precisely the reversible-but-wrong class. Refusing anything unrecognised keeps a
     * decline the only outcome for text this rule does not fully account for.
     */
    private fun drawCount(script: CardScript, requireTarget: Boolean): Int? {
        val effect = script.spellEffect as? DrawCardsEffect ?: return null
        if (script.copy(spellEffect = null, targetRequirements = emptyList()) != CardScript.EMPTY) return null
        val drawer = if (requireTarget) Targets.isBound(effect.target) else effect.target == EffectTarget.Controller
        if (!drawer) return null
        return (effect.count as? DynamicAmount.Fixed)?.amount
    }
}
