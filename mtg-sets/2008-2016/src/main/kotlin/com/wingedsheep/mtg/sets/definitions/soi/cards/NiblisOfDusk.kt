package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Niblis of Dusk (Shadows over Innistrad #76)
 * {2}{U}
 * Creature — Spirit
 * 2 / 1
 *
 * Flying
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 *
 * Both lines are engine-live keywords; the reminder text is printed oracle text, so it stays in
 * `oracleText` while the behaviour comes entirely from [Keyword.FLYING] and [Keyword.PROWESS].
 */
val NiblisOfDusk = card("Niblis of Dusk") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"

    keywords(Keyword.FLYING, Keyword.PROWESS)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Nils Hamm"
        flavorText = "It fuels its lanterns by leaching the warmth from its surroundings."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/394dd931-e34e-4314-88c9-774a2f3c8c1b.jpg?1783937792"
    }
}
