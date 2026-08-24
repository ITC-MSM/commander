package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Complete Disregard
 * {2}{B}
 * Instant
 * Devoid (This card has no color.)
 * Exile target creature with power 3 or less.
 *
 * Devoid is a characteristic-defining ability the SDK derives in `CardDefinition.colors`,
 * so the keyword alone is enough — the card is colorless in every zone while its colour
 * identity stays black.
 */
val CompleteDisregard = card("Complete Disregard") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Devoid (This card has no color.)\n" +
        "Exile target creature with power 3 or less."

    keywords(Keyword.DEVOID)

    spell {
        val creature = target("target creature", Targets.CreatureWithPowerAtMost(3))
        effect = Effects.Exile(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Peter Mohrbacher"
        flavorText = "\"We returned to the field and found poor Len, every detail of his final moment perfectly " +
            "cast in that awful dust.\"\n" +
            "—Javad Nasrin, outrider captain"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d94e878-6c49-4420-9d34-f9ee64e811ba.jpg?1783938206"
    }
}
