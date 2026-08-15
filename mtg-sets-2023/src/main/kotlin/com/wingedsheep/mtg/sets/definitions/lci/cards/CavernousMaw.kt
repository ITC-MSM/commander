package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cavernous Maw
 * Land — Cave (LCI #270, uncommon)
 *
 * {T}: Add {C}.
 * {2}: This land becomes a 3/3 Elemental creature until end of turn. It's still a Cave land.
 *   Activate only if the number of other Caves you control plus the number of Cave cards in
 *   your graveyard is three or greater.
 *
 * The animate is the standard "Restless"/creature-land shape ([RestlessRidgeline],
 * [TendrilOfTheMycotyrant]): a self-targeting [Effects.BecomeCreature] with base 3/3, creature
 * type Elemental, `Duration.EndOfTurn`. It keeps its existing types — no `removeTypes` — so it
 * stays a Land with the Cave subtype ("It's still a Cave land"), and stays colorless (no `colors`).
 *
 * The activation gate is an [ActivationRestriction.OnlyIfCondition] comparing a composite count to
 * three (CR 205.3i cave subtype). The count sums two [DynamicAmount]s:
 *   - **other Caves you control** — [DynamicAmount.AggregateBattlefield] with `excludeSelf = true`,
 *     so the Maw itself is not counted among the Caves it controls (the "other" in the oracle).
 *   - **Cave cards in your graveyard** — [DynamicAmount.Count] over [Zone.GRAVEYARD]
 *     ([GameObjectFilter.Any] with the Cave subtype, since graveyard cards aren't restricted to lands).
 * The activation-restriction check evaluates the condition with the Maw as the effect source
 * (`CastPermissionUtils.checkActivationRestriction`), so `excludeSelf` resolves against the Maw.
 */
val CavernousMaw = card("Cavernous Maw") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land — Cave"
    oracleText = "{T}: Add {C}.\n" +
        "{2}: This land becomes a 3/3 Elemental creature until end of turn. It's still a Cave land. " +
        "Activate only if the number of other Caves you control plus the number of Cave cards in " +
        "your graveyard is three or greater."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.BecomeCreature(
            target = EffectTarget.Self,
            power = 3,
            toughness = 3,
            creatureTypes = setOf("Elemental"),
            duration = Duration.EndOfTurn,
        )
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.CompareAmounts(
                    DynamicAmount.Add(
                        // other Caves you control (exclude this land itself)
                        DynamicAmount.AggregateBattlefield(
                            player = Player.You,
                            filter = GameObjectFilter.Land.withSubtype("Cave"),
                            aggregation = Aggregation.COUNT,
                            excludeSelf = true,
                        ),
                        // Cave cards in your graveyard
                        DynamicAmount.Count(
                            player = Player.You,
                            zone = Zone.GRAVEYARD,
                            filter = GameObjectFilter.Any.withSubtype("Cave"),
                        ),
                    ),
                    ComparisonOperator.GTE,
                    DynamicAmount.Fixed(3),
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "270"
        artist = "Alfven Ato"
        flavorText = "Many footprints lead into the cave. None lead out."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a51ebf6-a465-42e2-82b7-d2cb928ca632.jpg?1782694396"
    }
}
