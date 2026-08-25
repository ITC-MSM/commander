package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deft Duelist
 * {W}{U}
 * Creature — Human Rogue
 * 2 / 1
 * First strike
 * Shroud (This creature can't be the target of spells or abilities.)
 *
 * Both lines are simple keywords the engine already reads — shroud's reminder text is only reminder
 * text — so the card is a `keywords` declaration and nothing else.
 */
val DeftDuelist = card("Deft Duelist") {
    manaCost = "{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Rogue"
    power = 2
    toughness = 1
    oracleText = "First strike\n" +
        "Shroud (This creature can't be the target of spells or abilities.)"

    keywords(Keyword.FIRST_STRIKE, Keyword.SHROUD)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "David Palumbo"
        flavorText = "Some Serul Cove rogues traffic in stolen sigils, which fetch a high price from those who would rather pay for honor than earn it."
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4885a83-9410-4c88-b45e-1e1626fe90ea.jpg"
    }
}
