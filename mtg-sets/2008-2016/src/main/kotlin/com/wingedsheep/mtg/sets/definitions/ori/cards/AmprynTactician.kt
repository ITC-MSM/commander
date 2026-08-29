package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ampryn Tactician
 * {2}{W}{W}
 * Creature — Human Soldier
 * 3/3
 *
 * When this creature enters, creatures you control get +1/+1 until end of turn.
 *
 * A one-shot group pump, not a lord: [Patterns.Group.modifyStatsForAll] applies +1/+1 to each
 * creature you control as the trigger resolves, so creatures that arrive later this turn miss it.
 */
val AmprynTactician = card("Ampryn Tactician") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "When this creature enters, creatures you control get +1/+1 until end of turn."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Group.modifyStatsForAll(1, 1, Filters.Group.creaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Cynthia Sheppard"
        flavorText = "\"It's all a game. You shouldn't get too attached to the pieces.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82a2e1d9-6763-4024-a18b-982d96395553.jpg"
    }
}
