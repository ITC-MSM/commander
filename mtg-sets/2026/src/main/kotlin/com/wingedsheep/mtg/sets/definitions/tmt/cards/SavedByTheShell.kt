package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Saved by the Shell
 * {1}{G}
 * Instant
 *
 * This spell costs {1} less to cast if you control a Turtle.
 * Put a +1/+1 counter on target creature you control. It gains trample,
 * hexproof, and indestructible until end of turn.
 */
val SavedByTheShell = card("Saved by the Shell") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast if you control a Turtle.\nPut a +1/+1 counter on target creature you control. It gains trample, hexproof, and indestructible until end of turn."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 1,
                    filter = GameObjectFilter.Any.withSubtype("Turtle"),
                ),
            ),
        )
    }

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, creature, Duration.EndOfTurn))
            .then(Effects.GrantKeyword(Keyword.HEXPROOF, creature, Duration.EndOfTurn))
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature, Duration.EndOfTurn))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "132"
        artist = "Leonardo Santanna"
        flavorText = "\"I LOVE being a turtle!\"\n—Michelangelo"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6314c7f-41dc-4bbd-99db-3a8a1d2977b2.jpg?1771586974"
    }
}
