package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shining Aerosaur
 * {4}{W}
 * Creature — Dinosaur
 * 3/4
 *
 * Flying
 */
val ShiningAerosaur = card("Shining Aerosaur") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur"
    oracleText = "Flying"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Dan Murayama Scott"
        flavorText = "\"The invaders cloak themselves in the shadows of dusk. Aerosaurs hide in the brilliance of the noonday sun.\"\n—Caparocti Sunborn"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e900d0d-6f35-4e5d-9365-6ade227d218d.jpg"
    }
}
