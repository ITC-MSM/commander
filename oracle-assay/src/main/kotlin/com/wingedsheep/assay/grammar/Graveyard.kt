package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetRequirement

/**
 * Clauses that reach into a graveyard — "Return target creature card from your graveyard to your
 * hand."
 *
 * The first rules whose target is **not on the battlefield**, which is the whole reason they are a
 * file: [Targets.permanent] mints a battlefield `TargetObject` and its inverse refuses anything
 * else, deliberately, so that "destroy target creature" cannot print a script pointing at a
 * graveyard. A graveyard target is the same `TargetObject` with a `zone` and an owner predicate, and
 * both are printed by the noun phrase — "**your** graveyard" is the owner and "from your graveyard"
 * is the zone.
 *
 * The noun ends in "card" rather than naming a permanent, which is Oracle's own distinction: an
 * object in a graveyard is a *card*, not a permanent, so the printed form is "{filter} card" and the
 * bare type nouns [Filters] spells are the modifier in front of it.
 */
object Graveyard {

    /** "target creature card from your graveyard" — the requirement half. */
    private fun inYourGraveyard(filter: GameObjectFilter): TargetRequirement =
        TargetObject(filter = TargetFilter(filter.ownedByYou(), zone = Zone.GRAVEYARD), id = Targets.SLOT)

    /** The inverse: the filter a your-graveyard requirement restricts to, or null for anything else. */
    private fun graveyardFilter(requirement: TargetRequirement): GameObjectFilter? {
        val base = (requirement as? TargetObject)?.filter?.baseFilter ?: return null
        val unowned = base.copy(controllerPredicate = null)
        return unowned.takeIf { requirement == inYourGraveyard(it) }
    }

    /**
     * The shape: a verb, one card targeted in your graveyard, and nothing else.
     *
     * Two members — to your hand and onto the battlefield — differing only in the destination, which
     * is a different English sentence rather than a different word, so each spells its own template.
     */
    private fun graveyardStep(
        template: String,
        name: String,
        effect: (com.wingedsheep.sdk.scripting.targets.EffectTarget) -> com.wingedsheep.sdk.scripting.effects.Effect,
    ): Phrase<CardScript> {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = effect(Targets.bound()),
            targetRequirements = listOf(inYourGraveyard(filter)),
        )
        return phrase(template, name = name) {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = graveyardFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * "Return target card from your graveyard to your hand." — Elven Cache.
     *
     * `GameObjectFilter.Any` is what "card" with no modifier means, and [Filters] has no noun for it
     * on purpose: "card" is not a permanent type, and a row for it would let "destroy target card"
     * parse. So the unqualified form is its own rule rather than a filter the general one slots.
     */
    private val returnAnyCardToHand: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = Effects.Move(Targets.bound(), Zone.HAND),
            targetRequirements = listOf(inYourGraveyard(GameObjectFilter.Any)),
        )
        phrase("return target card from your graveyard to your hand", name = "return any card from your graveyard") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    val clauses: List<Phrase<CardScript>> = listOf(
        graveyardStep(
            "return target {filter} card from your graveyard to your hand",
            "return a card from your graveyard to your hand",
        ) { Effects.Move(it, Zone.HAND) },
        graveyardStep(
            "return target {filter} card from your graveyard to the battlefield",
            "return a card from your graveyard to the battlefield",
        ) { Effects.Move(it, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD) },
        returnAnyCardToHand,
    )
}
