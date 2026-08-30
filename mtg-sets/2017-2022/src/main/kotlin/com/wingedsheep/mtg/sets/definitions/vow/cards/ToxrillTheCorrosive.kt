package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/** VOW's own 1/1 black Slug token art. */
private const val SLUG_TOKEN_IMAGE =
    "https://cards.scryfall.io/normal/front/6/e/6e2ae34f-4558-46e0-95c5-e00d813fa355.jpg?1783924698"

/**
 * Toxrill, the Corrosive — Innistrad: Crimson Vow #132
 * {5}{B}{B} · Legendary Creature — Slug Horror · Mythic · 7/7
 *
 * At the beginning of each end step, put a slime counter on each creature you don't control.
 * Creatures you don't control get -1/-1 for each slime counter on them.
 * Whenever a creature you don't control with a slime counter on it dies, create a 1/1 black Slug
 *   creature token.
 * {U}{B}, Sacrifice a Slug: Draw a card.
 *
 * Modeling notes:
 *
 *  - **The slime counter is a pure marker** ([Counters.SLIME], added to `CounterType` with this
 *    card). It carries no rule of its own — Toxrill's *second* ability is the only thing that turns
 *    a tally into a P/T change, and his third only asks whether one is present. That split is what
 *    the printed rulings require: both abilities "apply to all creatures you don't control with
 *    slime counters, **even if those slime counters came from a source other than Toxrill's first
 *    ability**", and the third "triggers when a creature an opponent controls with a slime counter
 *    on it dies **for any reason**, not just due to its toughness being decreased".
 *  - **"for each slime counter on *them*" is a per-affected-permanent amount**, not a source
 *    tally. [GrantDynamicStatsEffect] is a Layer 7c bonus whose `DynamicAmount` is re-evaluated for
 *    every creature it touches, with `EffectContext.affectedEntityId` bound to that creature — so
 *    [EntityReference.AffectedEntity] reads *its own* counters. This is Withering Hex's expression
 *    (`Multiply(counterCount, -1)`, the negation idiom — there is no `Negate`) with `Source`
 *    swapped for `AffectedEntity`, and Diligent Zookeeper's per-affected-entity read widened from
 *    one creature to a group.
 *  - **The dies trigger reads last-known counters.** `Triggers.leavesBattlefield` with a
 *    `.withCounter` filter is matched against the `ZoneChangeEvent`'s last-known state when the
 *    creature came from the battlefield, so the counter is still visible even though the entity is
 *    already gone. A slimed creature that dies *because* Toxrill shrank it to zero toughness is the
 *    common case, and it works for exactly this reason.
 *  - **"Creatures you don't control"** is the corpus's `opponentControls()` (Wolf Strike, and every
 *    other VOW card with that wording).
 *  - **`Costs.Sacrifice`, not `SacrificeAnother`**: Toxrill is himself a Slug, so he is a legal
 *    sacrifice for his own ability.
 */
val ToxrillTheCorrosive = card("Toxrill, the Corrosive") {
    manaCost = "{5}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Slug Horror"
    power = 7
    toughness = 7
    oracleText = "At the beginning of each end step, put a slime counter on each creature you " +
        "don't control.\n" +
        "Creatures you don't control get -1/-1 for each slime counter on them.\n" +
        "Whenever a creature you don't control with a slime counter on it dies, create a 1/1 " +
        "black Slug creature token.\n" +
        "{U}{B}, Sacrifice a Slug: Draw a card."

    // At the beginning of each end step, put a slime counter on each creature you don't control.
    triggeredAbility {
        trigger = Triggers.EachEndStep
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesOpponentsControl,
            Effects.AddCounters(Counters.SLIME, 1, EffectTarget.Self)
        )
        description = "At the beginning of each end step, put a slime counter on each creature " +
            "you don't control."
    }

    // Creatures you don't control get -1/-1 for each slime counter on them.
    staticAbility {
        // Re-evaluated per affected creature: AffectedEntity is *that* creature, not Toxrill.
        val slimeOnIt = DynamicAmount.Multiply(
            DynamicAmount.EntityProperty(
                entity = EntityReference.AffectedEntity,
                numericProperty = EntityNumericProperty.CounterCount(
                    CounterTypeFilter.Named(Counters.SLIME)
                )
            ),
            -1
        )
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.AllCreaturesOpponentsControl,
            powerBonus = slimeOnIt,
            toughnessBonus = slimeOnIt
        )
    }

    // Whenever a creature you don't control with a slime counter on it dies, create a 1/1 black
    // Slug creature token.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls().withCounter(Counters.SLIME),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Slug"),
            imageUri = SLUG_TOKEN_IMAGE
        )
        description = "Whenever a creature you don't control with a slime counter on it dies, " +
            "create a 1/1 black Slug creature token."
    }

    // {U}{B}, Sacrifice a Slug: Draw a card.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{U}{B}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype("Slug"))
        )
        effect = Effects.DrawCards(1)
        description = "{U}{B}, Sacrifice a Slug: Draw a card."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "132"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84e64f38-b1f3-47cd-8cfb-a4861369aca3.jpg?1783924850"

        ruling("2021-11-19", "Toxrill, the Corrosive's second and third abilities apply to all creatures you don't control with slime counters, even if those slime counters came from a source other than Toxrill's first ability.")
        ruling("2021-11-19", "Toxrill's third ability triggers when a creature an opponent controls with a slime counter on it dies for any reason, not just due to its toughness being decreased by Toxrill's second ability.")
    }
}
