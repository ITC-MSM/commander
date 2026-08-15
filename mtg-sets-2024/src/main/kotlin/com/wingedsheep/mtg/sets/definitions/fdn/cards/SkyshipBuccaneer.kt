package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.core.Keyword

/**
 * Skyship Buccaneer
 * {3}{U}{U}
 * Creature — Human Pirate
 * 4/3
 *
 * Flying
 * Raid — When this creature enters, if you attacked this turn, draw a card.
 *
 * Raid is modeled as an enters-the-battlefield trigger gated by the intervening-if
 * condition [Conditions.YouAttackedThisTurn] (mirrors Gorehorn Raider / Mardu
 * Skullhunter). The condition is re-checked on resolution, so the trigger does
 * nothing if the "you attacked this turn" state no longer holds.
 */
val SkyshipBuccaneer = card("Skyship Buccaneer") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    power = 4
    toughness = 3
    oracleText = "Flying\nRaid — When this creature enters, if you attacked this turn, draw a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouAttackedThisTurn
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Javier Charro"
        flavorText = "\"It's a pirate's nature to be free of law. Gravity is no exception.\"\n—Kari Zev, skyship captain"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62958fc3-55dc-4b97-a070-490d6ed27820.jpg?1782689223"
    }
}
