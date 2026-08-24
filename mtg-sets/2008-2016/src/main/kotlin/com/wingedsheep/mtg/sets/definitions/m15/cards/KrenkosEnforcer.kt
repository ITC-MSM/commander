package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Krenko's Enforcer
 * {1}{R}{R}
 * Creature — Goblin Warrior
 * 2/2
 * Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)
 *
 * CR 702.13b — enforced by `IntimidateRule` in the engine's block-evasion chain.
 */
val KrenkosEnforcer = card("Krenko's Enforcer") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 2
    toughness = 2
    oracleText = "Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)"

    keywords(Keyword.INTIMIDATE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Karl Kopinski"
        flavorText = "He just likes to break legs. Collecting the debt is a bonus."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce2ceeb0-03b6-48bc-a084-069176ebccb2.jpg?1783939173"
    }
}
