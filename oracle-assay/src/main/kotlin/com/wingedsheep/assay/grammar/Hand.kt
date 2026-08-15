package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.DrawUpToEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.effects.LookAtTargetHandEffect
import com.wingedsheep.sdk.scripting.effects.RevealHandEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Clauses about hands — looking at one, making a player discard from one, drawing into one.
 *
 * Like [Library], most of these denote a `Patterns` recipe rather than a single effect, and the
 * rules build through the published facade and match by reconstructing it. The exception is looking
 * at a hand, which really is one effect.
 */
object Hand {

    /** "Look at target opponent's hand." — Sorcerous Sight. */
    private val lookAtOpponentHand: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = LookAtTargetHandEffect(Targets.bound()),
            targetRequirements = listOf(Targets.opponent()),
        )
        phrase("look at target opponent's hand", name = "look at target opponent's hand") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /** "Target opponent discards a card at random." — Mind Knives. */
    private val opponentDiscardsAtRandom: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = Patterns.Hand.discardRandom(1, Targets.bound()),
            targetRequirements = listOf(Targets.opponent()),
        )
        phrase("target opponent discards a card at random", name = "target opponent discards at random") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /** "Each opponent discards a card." — Noxious Toad's death trigger. */
    private val eachOpponentDiscards: Phrase<CardScript> = run {
        val script = CardScript(spellEffect = Patterns.Hand.eachOpponentDiscards(1))
        phrase("each opponent discards a card", name = "each opponent discards a card") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Each player may draw up to two cards. For each card less than two a player draws this way,
     * that player gains 2 life." — Temporary Truce.
     *
     * Two printed sentences and one rule, for [Library.lookAtOpponentTopAndBury]'s reason: the model
     * is a single `ForEachPlayer` iteration whose two inner steps are *linked by a pipeline slot*
     * ("cardsNotDrawn"), so the second sentence has no meaning without the first and the split
     * [Steps.sequence] performs would produce a half that denotes nothing.
     *
     * The card's limit appears twice in the text and once in the model, so the rule takes two slots
     * and refuses to build when they disagree — the honest reading of a sentence that states one
     * number in two places, and the only one that stays invertible.
     */
    private val eachPlayerMayDraw: Phrase<CardScript> = run {
        fun scriptFor(maximum: Int, life: Int) =
            CardScript(spellEffect = Patterns.Hand.eachPlayerMayDraw(maxCards = maximum, lifePerCardNotDrawn = life))
        phrase(
            "each player may draw up to {max} cards. for each card less than {limit} a player " +
                "draws this way, that player gains {life} life",
            name = "each player may draw up to N cards",
        ) {
            slot("max", Cardinals.word)
            slot("limit", Cardinals.word)
            slot("life", Primitives.cardinal)
            build { bindings ->
                val maximum = bindings.int("max")
                if (maximum != bindings.int("limit")) return@build null
                scriptFor(maximum, bindings.int("life"))
            }
            match { script ->
                val body = (script.spellEffect as? ForEachEffect)?.body as? CompositeEffect ?: return@match null
                val maximum = (body.effects.firstOrNull() as? DrawUpToEffect)?.maxCards ?: return@match null
                val gain = (body.effects.getOrNull(1) as? GainLifeEffect)?.amount as? DynamicAmount.Multiply
                    ?: return@match null
                if (!Cardinals.spellable(maximum)) return@match null
                if (script != scriptFor(maximum, gain.multiplier)) return@match null
                bind("max" to maximum, "limit" to maximum, "life" to gain.multiplier)
            }
        }
    }

    /** "Look at target player's hand." — Ingenious Thief; the same effect over the wider target. */
    private val lookAtPlayerHand: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = LookAtTargetHandEffect(Targets.bound()),
            targetRequirements = listOf(Targets.player()),
        )
        phrase("look at target player's hand", name = "look at target player's hand") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * The discards — "discard a card.", "Target player discards two cards.", "Have target opponent
     * discard a card."
     *
     * One recipe (`Patterns.Hand.discardCards`) with two variables, the count and who discards, and
     * English spells the second as the sentence's *subject* rather than as a word in a fixed
     * position — "target player discards" against "have target opponent discard". So the shape is a
     * template per subject, each carrying its own requirement, and the singular and plural counts
     * are two rows for the reason [Steps] gives: the article and the noun both change.
     */
    private fun discard(
        template: String,
        name: String,
        count: Int?,
        target: com.wingedsheep.sdk.scripting.targets.EffectTarget,
        requirements: List<com.wingedsheep.sdk.scripting.targets.TargetRequirement>,
    ): Phrase<CardScript> {
        fun scriptFor(cards: Int) = CardScript(
            spellEffect = Patterns.Hand.discardCards(cards, target),
            targetRequirements = requirements,
        )
        return phrase(template, name = name) {
            if (count == null) slot("n", Cardinals.word)
            build { bindings -> scriptFor(count ?: bindings.int("n")) }
            match { script ->
                val cards = count ?: discardedCount(script) ?: return@match null
                if (count == null && !Cardinals.spellable(cards)) return@match null
                if (script != scriptFor(cards)) return@match null
                bind("n" to cards)
            }
        }
    }

    /** How many cards a `discardCards` pipeline discards, read off its select step. */
    private fun discardedCount(script: CardScript): Int? {
        val select = (script.spellEffect as? CompositeEffect)?.effects
            ?.filterIsInstance<SelectFromCollectionEffect>()?.firstOrNull() ?: return null
        val mode = select.selection as? SelectionMode.ChooseExactly ?: return null
        return (mode.count as? DynamicAmount.Fixed)?.amount
    }

    /**
     * The whole-table hand effects — "Each player draws X cards.", "Each player discards any number
     * of cards, then draws that many cards.", the wheel.
     *
     * Each is one published recipe and one printed sentence with no variable in it, so each is a
     * constant rule: the reconstruction *is* the comparison, and a card whose pipeline differs in
     * any field declines rather than printing a sentence it does not mean.
     */
    private fun tableWide(template: String, name: String, effect: Effect): Phrase<CardScript> {
        val script = CardScript(spellEffect = effect)
        return phrase(template, name = name) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /** "Target opponent reveals their hand." — Baleful Stare's first sentence. */
    private val opponentRevealsHand: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = RevealHandEffect(Targets.bound()),
            targetRequirements = listOf(Targets.opponent()),
        )
        phrase("target opponent reveals their hand", name = "target opponent reveals their hand") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    val clauses: List<Phrase<CardScript>> = listOf(
        lookAtOpponentHand,
        opponentRevealsHand,
        lookAtPlayerHand,
        opponentDiscardsAtRandom,
        eachOpponentDiscards,
        eachPlayerMayDraw,
        discard(
            "discard a card", "discard a card",
            count = 1, target = EffectTarget.Controller, requirements = emptyList(),
        ),
        discard(
            "discard {n} cards", "discard cards",
            count = null, target = EffectTarget.Controller, requirements = emptyList(),
        ),
        discard(
            "target player discards a card", "target player discards a card",
            count = 1, target = Targets.bound(), requirements = listOf(Targets.player()),
        ),
        discard(
            "target player discards {n} cards", "target player discards cards",
            count = null, target = Targets.bound(), requirements = listOf(Targets.player()),
        ),
        discard(
            "have target opponent discard a card", "have target opponent discard a card",
            count = 1, target = Targets.bound(), requirements = listOf(Targets.opponent()),
        ),
        tableWide(
            "each player draws X cards", "each player draws X cards",
            Patterns.Hand.eachPlayerDrawsX(includeController = true, includeOpponents = true),
        ),
        tableWide(
            "each player discards any number of cards, then draws that many cards",
            "each player discards and redraws",
            Patterns.Hand.eachPlayerDiscardsDraws(),
        ),
        tableWide(
            "each player shuffles the cards from their hand into their library, then draws that many cards",
            "the wheel",
            Patterns.Hand.wheelEffect(Player.Each),
        ),
    )
}
