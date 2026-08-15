package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Knight of the Keep
 * {2}{W}
 * Creature — Human Knight
 * 3/2
 * (Vanilla)
 */
val KnightOfTheKeep = card("Knight of the Keep") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 2
    oracleText = ""

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "291"
        artist = "Nicholas Gregory"
        flavorText = "Now the cries of clear strong voices came ringing over the fields. They rode in: a long line of mail-clad Men, swift, shining, fell and fair to look upon. Suddenly they swept up with a noise like thunder, and the foremost horseman swerved."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3af21576-3793-4796-ab60-112fbbb5fc0d.jpg?1687424787"
    }
}
