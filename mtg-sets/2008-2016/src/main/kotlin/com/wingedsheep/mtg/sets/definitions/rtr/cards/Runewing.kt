package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Runewing
 * {3}{U}
 * Creature — Bird
 * 2/2
 *
 * Flying
 * When this creature dies, draw a card.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A dies trigger over the plain draw facade.
 */
val Runewing = card("Runewing") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    oracleText = "Flying\n" +
        "When this creature dies, draw a card."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Martina Pilcerova"
        flavorText = "In the hands of the open-minded, a runewing quill writes wisdom of its own."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/749961e6-b135-4629-ae9d-124de0d70db9.jpg?1783940366"
    }
}
