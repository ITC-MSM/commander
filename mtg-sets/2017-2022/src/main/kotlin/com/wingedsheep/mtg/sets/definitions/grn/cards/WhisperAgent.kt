package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Whisper Agent
 * {1}{U/B}{U/B}
 * Creature — Human Rogue
 * 3/2
 * Flash
 * When this creature enters, surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val WhisperAgent = card("Whisper Agent") {
    manaCost = "{1}{U/B}{U/B}"
    colorIdentity = "BU"
    typeLine = "Creature — Human Rogue"
    oracleText = "Flash\n" +
        "When this creature enters, surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"
    power = 3
    toughness = 2

    keywords(Keyword.FLASH)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.surveil(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "220"
        artist = "Deruchenko Alexander"
        flavorText = "He has a job to finish, and it's you."
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6d318e7-f49e-49f8-98b6-d62c34aa4af2.jpg?1783934113"
    }
}
