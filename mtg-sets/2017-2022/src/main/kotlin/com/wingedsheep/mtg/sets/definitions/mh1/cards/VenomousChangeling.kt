package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Venomous Changeling
 * {2}{B}
 * Creature — Shapeshifter
 * 1/3
 * Changeling (This card is every creature type.)
 * Deathtouch
 */
val VenomousChangeling = card("Venomous Changeling") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shapeshifter"
    power = 1
    toughness = 3
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Deathtouch"

    keywords(Keyword.CHANGELING, Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Aaron Miller"
        flavorText = "It doesn't contain venom. It is venom."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c5a1d73-d102-469b-82ca-ec18f616375e.jpg?1783933117"
    }
}
