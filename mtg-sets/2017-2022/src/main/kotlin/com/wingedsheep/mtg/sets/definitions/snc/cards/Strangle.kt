package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreatureOrPlaneswalker

/**
 * Strangle
 * {R}
 * Sorcery
 * Strangle deals 3 damage to target creature or planeswalker.
 */
val Strangle = card("Strangle") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Strangle deals 3 damage to target creature or planeswalker."

    spell {
        val victim = target("target creature or planeswalker", TargetCreatureOrPlaneswalker())
        effect = Effects.DealDamage(3, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Vincent Proce"
        flavorText = "They'd warned him greed would be the death of him. He never thought to take it literally."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b91d727-c0ee-4bf0-8c7d-8475ecb88083.jpg?1783923112"
    }
}
