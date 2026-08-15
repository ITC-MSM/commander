package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Searslicer Goblin
 * {1}{R}
 * Creature — Goblin Warrior
 * 2/1
 *
 * Raid — At the beginning of your end step, if you attacked this turn, create a
 * 1/1 red Goblin creature token.
 *
 * "Raid" is an ability word (no rules meaning); the actual gate is the intervening-if
 * condition that you attacked this turn, evaluated both when the trigger would be put on
 * the stack and again on resolution.
 */
val SearslicerGoblin = card("Searslicer Goblin") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 2
    toughness = 1
    oracleText = "Raid — At the beginning of your end step, if you attacked this turn, " +
        "create a 1/1 red Goblin creature token."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouAttackedThisTurn
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            imageUri = "https://cards.scryfall.io/normal/front/7/0/70f8a1de-cd4c-4afa-bf03-0245d375d42e.jpg?1782727474"
        )
        description = "At the beginning of your end step, if you attacked this turn, " +
            "create a 1/1 red Goblin creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "93"
        artist = "Wayne Reynolds"
        flavorText = "Ikko hadn't intended to start a riot, but now that it was under way, he was having a lot of fun."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94ad0b97-a318-4e76-ac79-b3e83417c333.jpg?1782689185"
    }
}
