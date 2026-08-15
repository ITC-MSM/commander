package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.effects.ScryEffect
import com.wingedsheep.sdk.scripting.effects.SurveilEffect
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

    // ---------------------------------------------------------------------------------------
    // One permanent, one verb
    // ---------------------------------------------------------------------------------------

    /**
     * The shape shared by "Destroy target creature.", "Exile target artifact.", "Tap target
     * creature you control." — a verb, one targeted permanent, and nothing else.
     *
     * The `match` half is an **equality test against what `build` would have produced**, not a
     * structural walk. That is deliberate and it is the discipline the whole file follows: a matcher
     * that inspected only the fields it cared about would happily print a script carrying extra
     * content it never looked at, which round-trips and loses meaning — the reversible-but-wrong
     * class. Reconstructing the whole script and comparing makes the check exhaustive by
     * construction, so a rule cannot fall behind the effect it prints.
     */
    private fun targetedPermanentStep(
        template: String,
        name: String,
        effect: (EffectTarget) -> Effect,
    ): Phrase<CardScript> {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = effect(Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        return phrase(template, name = name) {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // Counted verbs — "scry 2.", "you gain 3 life.", "target player gains 3 life."
    // ---------------------------------------------------------------------------------------

    /**
     * A verb whose only variable is a number written in **digits**.
     *
     * Digits, not [Cardinals.word]: Oracle spells a *quantity of cards* as a word ("draw two cards")
     * and a *quantity of life, damage or counters* as a numeral ("you gain 3 life", "deals 2
     * damage"). The two are different conventions in the same text, which is why the draw rules
     * above take one leaf and these take the other — and why neither can borrow the other's, in
     * either direction.
     *
     * The shape takes both halves of the inversion explicitly. `script` is the forward direction and
     * `count` reads the number back out of the effect, because there is no general way to invert an
     * arbitrary builder. Everything *else* the script might carry is still checked the fail-closed
     * way the rest of this file is: `count` only recovers the number, and the equality against
     * `script(n)` is what refuses to print a script carrying anything the sentence does not say.
     */
    private fun countedStep(
        template: String,
        name: String,
        script: (Int) -> CardScript,
        count: (Effect) -> Int?,
    ): Phrase<CardScript> = phrase(template, name = name) {
        slot("n", Primitives.cardinal)
        build { script(it.int("n")) }
        match { model ->
            val amount = count(model.spellEffect ?: return@match null) ?: return@match null
            if (model != script(amount)) return@match null
            bind("n" to amount)
        }
    }

    private val countedSteps: List<Phrase<CardScript>> = listOf(
        countedStep(
            "you gain {n} life.", "you gain life",
            script = { CardScript(spellEffect = Effects.GainLife(it)) },
            count = ::lifeGained,
        ),
        countedStep(
            "target player gains {n} life.", "target player gains life",
            script = {
                CardScript(
                    spellEffect = Effects.GainLife(it, Targets.bound()),
                    targetRequirements = listOf(Targets.player()),
                )
            },
            count = ::lifeGained,
        ),
        countedStep(
            "you lose {n} life.", "you lose life",
            script = { CardScript(spellEffect = Effects.LoseLife(it, EffectTarget.Controller)) },
            count = ::lifeLost,
        ),
        countedStep(
            "target player loses {n} life.", "target player loses life",
            script = {
                CardScript(
                    spellEffect = Effects.LoseLife(it, Targets.bound()),
                    targetRequirements = listOf(Targets.player()),
                )
            },
            count = ::lifeLost,
        ),
        countedStep(
            "scry {n}.", "scry",
            script = { CardScript(spellEffect = Effects.Scry(it)) },
            count = { (it as? ScryEffect)?.count },
        ),
        countedStep(
            "surveil {n}.", "surveil",
            script = { CardScript(spellEffect = Effects.Surveil(it)) },
            count = { (it as? SurveilEffect)?.count },
        ),
        countedStep(
            "${Normalizer.SELF} deals {n} damage to any target.", "deals damage to any target",
            script = {
                CardScript(
                    spellEffect = Effects.DealDamage(it, Targets.bound()),
                    targetRequirements = listOf(Targets.any()),
                )
            },
            count = ::damageDealt,
        ),
        countedStep(
            "${Normalizer.SELF} deals {n} damage to target player.", "deals damage to target player",
            script = {
                CardScript(
                    spellEffect = Effects.DealDamage(it, Targets.bound()),
                    targetRequirements = listOf(Targets.player()),
                )
            },
            count = ::damageDealt,
        ),
    )

    // ---------------------------------------------------------------------------------------
    // A count and a filtered target together
    // ---------------------------------------------------------------------------------------

    /**
     * "~ deals 2 damage to target creature." — the same verb as above over a noun phrase rather than
     * over the fixed "any target" / "target player" forms, so it carries two slots instead of one.
     *
     * Kept as its own rule rather than folded into [countedStep]: a shape parameterized over *both*
     * the count and the filter would have to thread the filter through the same `script`/`count`
     * pair, and there is exactly one member of it so far. Written inline until the second appears,
     * per the module's own rule about when to factor.
     */
    private val damageToTargetPermanent: Phrase<CardScript> = run {
        fun scriptFor(amount: Int, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.DealDamage(amount, Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase(
            "${Normalizer.SELF} deals {n} damage to target {filter}.",
            name = "deals damage to target permanent",
        ) {
            slot("n", Primitives.cardinal)
            slot("filter", Filters.filter)
            build { scriptFor(it.int("n"), it.value("filter")) }
            match { script ->
                val amount = damageDealt(script.spellEffect ?: return@match null) ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(amount, filter)) return@match null
                bind("n" to amount, "filter" to filter)
            }
        }
    }

    /**
     * "Target creature gets +3/+3 until end of turn." — the pump spell.
     *
     * The duration is spelled by the template and *not* by a slot: `Duration.EndOfTurn` is
     * `ModifyStats`'s default, and every other duration the SDK has ("as long as", "until your next
     * turn", `WhileSourceTapped`) is a different sentence rather than a different word in this one.
     * The reconstruct-and-compare in `match` is what makes that safe — a script whose duration is
     * anything else refuses to print here rather than losing the distinction.
     */
    private val pumpTargetPermanent: Phrase<CardScript> = run {
        fun scriptFor(modifiers: Pair<Int, Int>, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ModifyStats(modifiers.first, modifiers.second, Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase("target {filter} gets {mod} until end of turn.", name = "pump target") {
            slot("filter", Filters.filter)
            slot("mod", Primitives.statModifiers)
            build { scriptFor(it.value("mod"), it.value("filter")) }
            match { script ->
                val effect = script.spellEffect as? ModifyStatsEffect ?: return@match null
                val power = (effect.powerModifier as? DynamicAmount.Fixed)?.amount ?: return@match null
                val toughness = (effect.toughnessModifier as? DynamicAmount.Fixed)?.amount ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                val modifiers = power to toughness
                if (script != scriptFor(modifiers, filter)) return@match null
                bind("filter" to filter, "mod" to modifiers)
            }
        }
    }

    private val permanentSteps: List<Phrase<CardScript>> = listOf(
        targetedPermanentStep("destroy target {filter}.", "destroy target") { Effects.Destroy(it) },
        targetedPermanentStep("exile target {filter}.", "exile target") { Effects.Exile(it) },
        targetedPermanentStep("tap target {filter}.", "tap target") { Effects.Tap(it) },
        targetedPermanentStep("untap target {filter}.", "untap target") { Effects.Untap(it) },
        targetedPermanentStep(
            "return target {filter} to its owner's hand.",
            "return target to hand",
        ) { Effects.ReturnToHand(it) },
    )

    /** Every step rule, as one alternation. Grows with the family. */
    val all: List<Phrase<CardScript>> =
        listOf(drawOne, drawMany, targetPlayerDrawsOne, targetPlayerDrawsMany) +
            countedSteps + damageToTargetPermanent + pumpTargetPermanent + permanentSteps

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
    /**
     * The fixed amounts the counted verbs read back.
     *
     * Each recovers only the *number*; nothing here checks the target or the rest of the script,
     * because [countedStep]'s equality against its own `script(n)` already does, exhaustively. A
     * dynamic amount ("equal to the number of…") has no numeral to print, so it declines here.
     */
    private fun lifeGained(effect: Effect): Int? = (effect as? GainLifeEffect)?.amount?.fixed()

    private fun lifeLost(effect: Effect): Int? = (effect as? LoseLifeEffect)?.amount?.fixed()

    private fun damageDealt(effect: Effect): Int? = (effect as? DealDamageEffect)?.amount?.fixed()

    private fun DynamicAmount.fixed(): Int? = (this as? DynamicAmount.Fixed)?.amount

    private fun drawCount(script: CardScript, requireTarget: Boolean): Int? {
        val effect = script.spellEffect as? DrawCardsEffect ?: return null
        if (script.copy(spellEffect = null, targetRequirements = emptyList()) != CardScript.EMPTY) return null
        val drawer = if (requireTarget) Targets.isBound(effect.target) else effect.target == EffectTarget.Controller
        if (!drawer) return null
        return (effect.count as? DynamicAmount.Fixed)?.amount
    }
}
