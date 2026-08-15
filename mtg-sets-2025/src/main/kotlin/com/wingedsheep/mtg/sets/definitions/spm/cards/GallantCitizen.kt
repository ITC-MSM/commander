package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Gallant Citizen
 * {G/W}{G/W}
 * Creature — Human Citizen
 * 1/1
 * When this creature enters, draw a card.
 */
val GallantCitizen = card("Gallant Citizen") {
    manaCost = "{G/W}{G/W}"
    colorIdentity = "WG"
    typeLine = "Creature — Human Citizen"
    oracleText = "When this creature enters, draw a card."
    power = 1
    toughness = 1
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = DrawCardsEffect(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Allen Morris"
        flavorText = "\"People cheer for that webbed menace. They should be cheering for my son.\"\n—J. Jonah Jameson"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43790471-6ec8-4c1d-b6d3-74c6cdf8ce43.jpg?1757377671"
    }
}
