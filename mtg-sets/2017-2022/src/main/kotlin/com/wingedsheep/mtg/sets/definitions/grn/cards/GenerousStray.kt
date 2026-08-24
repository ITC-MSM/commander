package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Generous Stray
 * {2}{G}
 * Creature — Cat
 * 1/2
 * When this creature enters, draw a card.
 */
val GenerousStray = card("Generous Stray") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    oracleText = "When this creature enters, draw a card."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Milivoj Ćeran"
        flavorText = "Cats place their gifts with care, so that a bare foot will step on them in the middle of the night."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/3289db66-231f-4370-aca6-644d75bee293.jpg?1783934152"
    }
}
