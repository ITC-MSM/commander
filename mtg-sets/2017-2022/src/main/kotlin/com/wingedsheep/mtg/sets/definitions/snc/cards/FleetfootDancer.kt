package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fleetfoot Dancer
 * {1}{R}{G}{W}
 * Creature — Elf Druid
 * 4 / 4
 * Trample, lifelink, haste
 */
val FleetfootDancer = card("Fleetfoot Dancer") {
    manaCost = "{1}{R}{G}{W}"
    colorIdentity = "GRW"
    typeLine = "Creature — Elf Druid"
    oracleText = "Trample, lifelink, haste"
    power = 4
    toughness = 4

    keywords(Keyword.TRAMPLE, Keyword.LIFELINK, Keyword.HASTE)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "188"
        artist = "Joshua Raphael"
        flavorText = "The Vanto Beatdown began as an improvised dance-battle at the Rose Room lounge. When word spread of how many were slain at the first event, it became a citywide obsession."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/2473d738-fe15-402a-ad24-0d6e5c4dfda3.jpg?1783923085"
    }
}
