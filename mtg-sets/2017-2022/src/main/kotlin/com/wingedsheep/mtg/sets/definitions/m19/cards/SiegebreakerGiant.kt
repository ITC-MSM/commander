package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Siegebreaker Giant
 * {3}{R}{R}
 * Creature — Giant Warrior
 * 6/3
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 * {3}{R}: Target creature can't block this turn.
 */
val SiegebreakerGiant = card("Siegebreaker Giant") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 6
    toughness = 3
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)\n" +
        "{3}{R}: Target creature can't block this turn."

    keywords(Keyword.TRAMPLE)

    activatedAbility {
        cost = Costs.Mana("{3}{R}")
        val creature = target("target", TargetCreature())
        effect = Effects.CantBlock(creature)
        description = "{3}{R}: Target creature can't block this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "157"
        artist = "Even Amundsen"
        flavorText = "No rampart can withstand the fury of a giant."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ede2b911-8eec-4993-ab1c-59b55dfb11b4.jpg"
    }
}
