package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Pharika's Disciple
 * {3}{G}
 * Creature — Centaur Warrior
 * 2/3
 *
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 * Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)
 */
val PharikaSDisciple = card("Pharika's Disciple") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Warrior"
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)\n" +
        "Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)"
    power = 2
    toughness = 3

    keywords(Keyword.DEATHTOUCH)
    keywordAbility(KeywordAbility.renown(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "194"
        artist = "Karl Kopinski"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0b3d8f7-6a41-49ba-b111-d34a345394c0.jpg"
    }
}
