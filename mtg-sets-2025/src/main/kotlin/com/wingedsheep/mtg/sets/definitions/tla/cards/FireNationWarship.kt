package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Fire Nation Warship
 * {3}
 * Artifact — Vehicle
 * 4/4
 *
 * Reach
 * When this Vehicle dies, create a Clue token. (It's an artifact with
 *   "{2}, Sacrifice this token: Draw a card.")
 * Crew 2 (Tap any number of creatures you control with total power 2 or more:
 *   This Vehicle becomes an artifact creature until end of turn.)
 */
val FireNationWarship = card("Fire Nation Warship") {
    manaCost = "{3}"
    typeLine = "Artifact — Vehicle"
    power = 4
    toughness = 4
    oracleText = "Reach\n" +
        "When this Vehicle dies, create a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")\n" +
        "Crew 2 (Tap any number of creatures you control with total power 2 or more: " +
        "This Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateClue()
    }

    keywordAbility(KeywordAbility.crew(2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "256"
        artist = "Hisashi Momose"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41ecc1de-353e-4131-b735-d2ce18c9c2d5.jpg?1764121892"
    }
}
