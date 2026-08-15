package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeTargetEffect
import com.wingedsheep.sdk.scripting.effects.TurnFaceDownEffect
import com.wingedsheep.sdk.scripting.effects.TurnFaceUpEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Clauses that turn a permanent over — the payoff half of morph.
 *
 * Morph itself is a [Keywords] row (`Morph {2}{U}`) and the trigger it pays off is a [Triggers] row
 * ("When ~ is turned face up, …"); what lives here is the third piece, the *effects* that turn some
 * other permanent face down or face up. They are a family because they share a vocabulary the rest
 * of the grammar has no use for — "face-down creature you control", "creature with a morph ability"
 * — and because face-down-ness is a `StatePredicate` rather than a card predicate, so it reaches
 * [Filters] through a type noun rather than through a layer.
 */
object Morph {

    /** "Turn ~ face down." — Wall of Deceit, hiding itself again. */
    private val turnSelfFaceDown: Phrase<CardScript> = run {
        val script = CardScript(spellEffect = TurnFaceDownEffect(EffectTarget.Self))
        phrase("turn {self} face down", name = "turn the source face down") {
            slot("self", Primitives.self)
            build { script }
            match { if (it == script) bind("self" to Unit) else null }
        }
    }

    /**
     * "Turn target creature with a morph ability face down." — Master of the Veil.
     *
     * The noun phrase is spelled here rather than in [Filters] because "with a morph ability" is two
     * `StatePredicate`s — having the ability *and* being face up — for one printed phrase, and
     * nothing in the words says the second. A card can only be turned face down if it is face up,
     * so the redundancy is the templating's rather than the model's; slotting the noun would need a
     * filter layer that owned both predicates at once, which is exactly the composed-rather-than-
     * layered shape [Filters] refuses.
     */
    private val morphFilter: GameObjectFilter = GameObjectFilter.Creature.withMorph().faceUp()

    private val turnTargetFaceDown: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = TurnFaceDownEffect(Targets.bound()),
            targetRequirements = listOf(TargetPermanent(filter = TargetFilter(morphFilter), id = Targets.SLOT)),
        )
        phrase("turn target creature with a morph ability face down", name = "turn a target face down") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Turn any number of target creatures with morph abilities other than ~ face down." — Weaver of
     * Lies.
     *
     * Positional rather than named for [Combat.returnOneOrTwoTargets]' reason: the iteration rebinds
     * slot 0 per target, so a named reference would name the whole declaration. "Any number of" is
     * the requirement's `unlimited` flag and "other than ~" is its `excludeSelf`, which is why both
     * are literals — each is one field and one printed phrase, with nothing to vary.
     */
    private val turnAnyNumberFaceDown: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = ForEachTargetEffect(listOf(TurnFaceDownEffect(EffectTarget.ContextTarget(0)))),
            targetRequirements = listOf(
                TargetPermanent(
                    unlimited = true,
                    filter = TargetFilter(morphFilter).other(),
                    id = Targets.SLOT,
                )
            ),
        )
        phrase(
            "turn any number of target creatures with morph abilities other than {self} face down",
            name = "turn any number of targets face down",
        ) {
            slot("self", Primitives.self)
            build { script }
            match { if (it == script) bind("self" to Unit) else null }
        }
    }

    /**
     * "Turn target face-down creature you control face up. At the beginning of the next end step,
     * sacrifice it." — Skirk Alarmist.
     *
     * Two printed sentences and one rule, for [Library.lookAtOpponentTopAndBury]'s reason: the "it"
     * of the second sentence is the target the first one turned up, and the delayed trigger carries
     * that reference. Splitting the text would produce a second half that denotes a sacrifice of
     * nothing in particular.
     */
    private val turnTargetFaceUpAndSacrifice: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = TurnFaceUpEffect(Targets.bound()).then(
                CreateDelayedTriggerEffect(step = Step.END, effect = SacrificeTargetEffect(Targets.bound()))
            ),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase(
            "turn target {filter} face up. at the beginning of the next end step, sacrifice it",
            name = "turn a target face up and sacrifice it later",
        ) {
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

    val clauses: List<Phrase<CardScript>> = listOf(
        turnSelfFaceDown,
        turnTargetFaceDown,
        turnAnyNumberFaceDown,
        turnTargetFaceUpAndSacrifice,
    )
}
