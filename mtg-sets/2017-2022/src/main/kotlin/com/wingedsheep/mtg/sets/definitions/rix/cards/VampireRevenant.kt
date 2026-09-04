package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vampire Revenant
 * {3}{B}
 * Creature — Vampire Spirit
 * 3/1
 * Flying
 */
val VampireRevenant = card("Vampire Revenant") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Spirit"
    oracleText = "Flying"
    power = 3
    toughness = 1

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Nils Hamm"
        flavorText = "\"A thick fog obscures the port of Leor, but it's not thick enough to muffle the screams.\"\n—Admiral Beckett Brass"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bd3a6c6-33b8-4530-9d80-c488898afd6e.jpg?1783935304"
    }
}
