package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity


/**
 * Dragoon's Wyvern
 * {2}{U}
 * Creature — Drake
 * 2/1
 * Flying
 * When this creature enters, create a 1/1 colorless Hero creature token.
 */
val DragoonsWyvern = card("Dragoon's Wyvern") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flying\nWhen this creature enters, create a 1/1 colorless Hero creature token."
    power = 2
    toughness = 1
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Hero"),
            imageUri = "https://cards.scryfall.io/normal/front/d/0/d0657ce1-bf75-4007-ac1b-0623eb263357.jpg?1748704030",
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Jason Kiantoro"
        flavorText = "\"I heard that to become a dragoon, you had to make a pact with a living wyvern. However, thanks to the dragonslayers, there aren't that many dragons left.\"\n—Ceraulian, San d'Oria citizen"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d92bce20-308e-4841-aaf8-8e20698292e7.jpg"
    }
}
