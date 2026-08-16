package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Spark Reaper
 * {2}{B}
 * Creature — Zombie
 * 2/3
 * {3}, Sacrifice a creature or planeswalker: You gain 1 life and draw a card.
 */
val SparkReaper = card("Spark Reaper") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 3
    oracleText = "{3}, Sacrifice a creature or planeswalker: You gain 1 life and draw a card."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}"),
            Costs.Sacrifice(GameObjectFilter.CreatureOrPlaneswalker),
        )
        effect = Effects.Composite(
            Effects.GainLife(1),
            Effects.DrawCards(1),
        )
        description = "{3}, Sacrifice a creature or planeswalker: You gain 1 life and draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Zoltan Boros"
        flavorText = "\"I know they're unstoppable fighters created to harvest souls—it's just they're so rude about it.\" —Kaya"
        imageUri = "https://cards.scryfall.io/normal/front/9/2/922537f0-4caf-481c-b431-826f0c44e5c5.jpg?1783933437"
    }
}
