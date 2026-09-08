package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.championCreature
import com.wingedsheep.sdk.model.Rarity

/**
 * Changeling Hero
 * {4}{W}
 * Creature — Shapeshifter
 * 4/4
 *
 * Changeling
 * Champion a creature
 * Lifelink
 */
val ChangelingHero = card("Changeling Hero") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Shapeshifter"
    power = 4
    toughness = 4
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Champion a creature (When this enters, sacrifice it unless you exile another creature " +
        "you control. When this leaves the battlefield, that card returns to the battlefield.)\n" +
        "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.CHANGELING, Keyword.LIFELINK)
    championCreature()

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Jeff Miracola"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24f0c586-a1a5-4907-801a-7815c4dceb39.jpg?1783942917"
    }
}
