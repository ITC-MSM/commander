package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Coralhelm Guide
 * {1}{U}
 * Creature — Merfolk Scout Ally
 * 2/1
 * {4}{U}: Target creature can't be blocked this turn.
 */
val CoralhelmGuide = card("Coralhelm Guide") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Scout Ally"
    power = 2
    toughness = 1
    oracleText = "{4}{U}: Target creature can't be blocked this turn."

    activatedAbility {
        cost = Costs.Mana("{4}{U}")
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Viktor Titov"
        flavorText = "\"She knows every step of this coastline, both above and below the surface, and she has " +
            "hideouts all along the way. She will get you there.\"\n" +
            "—Jori En, expedition leader"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33787a5b-d1d1-4d60-ba09-d9c98025e9b3.jpg?1783938210"
    }
}
