package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jaya's Firenado
 * {4}{R}
 * Sorcery
 * Jaya's Firenado deals 5 damage to target creature or planeswalker. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val JayasFirenado = card("Jaya's Firenado") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Jaya's Firenado deals 5 damage to target creature or planeswalker. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.Composite(
            Effects.DealDamage(5, t),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Jeremy Wilson"
        flavorText = "\"For all their supposed advances, the Phyrexians still aren't fireproof.\"\n—Jaya Ballard"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b78eaab-819c-4f06-b3b1-a25c17ab2235.jpg?1783921314"
    }
}
