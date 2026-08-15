package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty

/**
 * The Skullspore Nexus {6}{G}{G}
 * Legendary Artifact
 *
 * This spell costs {X} less to cast, where X is the greatest power among creatures you control.
 * Whenever one or more nontoken creatures you control die, create a green Fungus Dinosaur creature
 * token with base power and toughness each equal to the total power of those creatures.
 * {2}, {T}: Double target creature's power until end of turn.
 *
 * Modeled per the Comprehensive Rules and Scryfall rulings:
 *  - Cost reduction: a self-cost [ModifySpellCost] with the
 *    [CostReductionSource.GreatestPropertyAmongPermanentsYouControl] (`EntityNumericProperty.Power`)
 *    dynamic source over creatures you
 *    control. Read from projected state, so a creature whose power is defined by another value
 *    (e.g. cards in hand) contributes its current power. Cannot reduce the {G}{G} pips or go below 0.
 *  - Death trigger: a batched [Triggers.OneOrMoreCreaturesYouControlDie] over *nontoken* creatures
 *    (CR 603.2c — fires once per death batch, not once per creature). The token's base power and
 *    toughness are both [DynamicAmounts.diedBatchTotalPower], the summed *last-known* power of the
 *    nontoken creatures that died (ruling 2023-11-10: "as they last existed on the battlefield").
 *  - Double power: `+X/+0` where X is the target's power as the ability resolves
 *    (ruling 2023-11-10), i.e. [DynamicAmounts.targetPower] for power and a fixed 0 toughness.
 */
val TheSkullsporeNexus = card("The Skullspore Nexus") {
    manaCost = "{6}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Artifact"
    oracleText = "This spell costs {X} less to cast, where X is the greatest power among creatures " +
        "you control.\n" +
        "Whenever one or more nontoken creatures you control die, create a green Fungus Dinosaur " +
        "creature token with base power and toughness each equal to the total power of those creatures.\n" +
        "{2}, {T}: Double target creature's power until end of turn."

    // "This spell costs {X} less to cast, where X is the greatest power among creatures you control."
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.GreatestPropertyAmongPermanentsYouControl(
                    EntityNumericProperty.Power, Filters.Creature
                )
            ),
        )
    }

    // "Whenever one or more nontoken creatures you control die, create a green Fungus Dinosaur
    //  creature token with base power and toughness each equal to the total power of those creatures."
    triggeredAbility {
        trigger = Triggers.OneOrMoreCreaturesYouControlDie(GameObjectFilter.Creature.nontoken())
        effect = Effects.CreateDynamicToken(
            dynamicPower = DynamicAmounts.diedBatchTotalPower(),
            dynamicToughness = DynamicAmounts.diedBatchTotalPower(),
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Fungus", "Dinosaur"),
            imageUri = "https://cards.scryfall.io/normal/front/f/9/f92f2858-e524-4d23-b947-d4dff20e7f59.jpg?1783913606"
        )
    }

    // "{2}, {T}: Double target creature's power until end of turn."
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target("creature whose power to double", Targets.Creature)
        effect = Effects.ModifyStats(
            power = DynamicAmounts.targetPower(0),
            toughness = DynamicAmount.Fixed(0),
            target = creature
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "212"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b56a7631-5f94-468d-aab7-7e9e129c5f49.jpg?1782694440"
        ruling(
            "2023-11-10",
            "Use the power of the nontoken creatures that died as they last existed on the " +
                "battlefield to determine the base power and toughness of the token created by the " +
                "middle ability."
        )
        ruling(
            "2023-11-10",
            "To double a creature's power, that creature gets +X/+0, where X is that creature's " +
                "power as The Skullspore Nexus's activated ability resolves."
        )
    }
}
