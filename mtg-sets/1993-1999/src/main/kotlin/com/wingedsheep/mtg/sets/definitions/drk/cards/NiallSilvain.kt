package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Niall Silvain
 * {G}{G}{G}
 * Creature — Ouphe
 * 2/2
 * {G}{G}{G}{G}, {T}: Regenerate target creature.
 */
val NiallSilvain = card("Niall Silvain") {
    manaCost = "{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ouphe"
    power = 2
    toughness = 2
    oracleText = "{G}{G}{G}{G}, {T}: Regenerate target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}{G}{G}{G}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = RegenerateEffect(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "82"
        artist = "Christopher Rush"
        flavorText = "This is his domain, and while you remain here you must value all life as you value your own."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d5911b5-a54e-4ebb-9c36-d4dc8e97bb4b.jpg?1783947931"
    }
}
