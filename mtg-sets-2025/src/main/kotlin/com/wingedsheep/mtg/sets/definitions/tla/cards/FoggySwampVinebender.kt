package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Foggy Swamp Vinebender
 * {3}{G}
 * Creature — Human Plant Ally
 * 4/3
 *
 * This creature can't be blocked by creatures with power 2 or less.
 * Waterbend {5}: Put a +1/+1 counter on this creature. Activate only during your turn. (While
 * paying a waterbend cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)
 *
 * Implementation notes:
 *  - The evasion clause is the [CantBeBlockedBy] static ability filtered to creatures with power
 *    at most 2 ([GameObjectFilter.Creature.powerAtMost]).
 *  - "Waterbend {5}" is an activated ability whose mana cost carries the waterbend
 *    alternative-cost flag ([com.wingedsheep.sdk.scripting.ActivatedAbility] `hasWaterbend`); the
 *    reminder text (tap artifacts/creatures to pay {1} each) is supplied by the flag.
 *  - "Activate only during your turn" is [ActivationRestriction.OnlyDuringYourTurn].
 */
val FoggySwampVinebender = card("Foggy Swamp Vinebender") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Plant Ally"
    oracleText = "This creature can't be blocked by creatures with power 2 or less.\n" +
        "Waterbend {5}: Put a +1/+1 counter on this creature. Activate only during your turn. " +
        "(While paying a waterbend cost, you can tap your artifacts and creatures to help. " +
        "Each one pays for {1}.)"
    power = 4
    toughness = 3

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    // Waterbend {5}: Put a +1/+1 counter on this creature. Activate only during your turn.
    activatedAbility {
        cost = Costs.Mana("{5}")
        hasWaterbend = true
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "Maël Ollivier-Henry"
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78a75317-94f7-47a4-b4da-5a027fa73248.jpg?1764121222"
    }
}
