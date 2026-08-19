package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brindle Shoat
 * {1}{G}
 * Creature — Boar
 * 1/1
 *
 * When this creature dies, create a 3/3 green Boar creature token.
 */
val BrindleShoat = card("Brindle Shoat") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    oracleText = "When this creature dies, create a 3/3 green Boar creature token."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Boar"),
        )
        description = "When this creature dies, create a 3/3 green Boar creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "60"
        artist = "Steven Belledin"
        flavorText = "Hunters lure the stripling boar into the open, hoping to trap greater prey."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e40569c-640b-4a1a-a586-bde37de5591f.jpg?1783940614"
    }
}
