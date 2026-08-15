package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Venomized Cat
 * {2}{B}
 * Creature — Symbiote Cat Villain
 * 2/3
 * Deathtouch
 * When this creature enters, mill two cards. (Put the top two cards of your library into your graveyard.)
 */
val VenomizedCat = card("Venomized Cat") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Symbiote Cat Villain"
    oracleText = "Deathtouch\nWhen this creature enters, mill two cards. (Put the top two cards of your library into your graveyard.)"
    power = 2
    toughness = 3
    keywords(Keyword.DEATHTOUCH)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(2)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Jessica Fong"
        flavorText = "New York City's rat problem disappeared overnight."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6330f3e9-e031-4d55-b5d8-536c16bba063.jpg?1757377211"
    }
}
