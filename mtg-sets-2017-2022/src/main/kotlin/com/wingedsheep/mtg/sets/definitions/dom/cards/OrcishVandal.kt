package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Orcish Vandal
 * {1}{R}
 * Creature — Orc Warrior
 * 1/1
 * {T}, Sacrifice an artifact: This creature deals 2 damage to any target.
 */
val OrcishVandal = card("Orcish Vandal") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Orc Warrior"
    power = 1
    toughness = 1
    oracleText = "{T}, Sacrifice an artifact: This creature deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.Sacrifice(GameObjectFilter.Artifact))
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "137"
        artist = "Alex Konstad"
        flavorText = "\"Every ancient relic is a weapon if you throw it hard enough.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/1/6192b4ea-f781-4c56-89bc-530f5388b6b5.jpg?1562736648"
    }
}
