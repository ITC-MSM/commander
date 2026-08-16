package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Goldnight Commander
 * {3}{W}
 * Creature — Human Cleric Soldier
 * 2/2
 * Whenever another creature you control enters, creatures you control get +1/+1 until end of turn.
 *
 * The group is gathered when the ability *resolves*, so the creature that entered is pumped too
 * if it is still on the battlefield then — but the trigger itself is [TriggerBinding.OTHER], so
 * Goldnight Commander's own arrival doesn't trigger it.
 */
val GoldnightCommander = card("Goldnight Commander") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric Soldier"
    power = 2
    toughness = 2
    oracleText = "Whenever another creature you control enters, creatures you control get +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Patterns.Group.modifyStatsForAll(1, 1, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "22"
        artist = "Chris Rahn"
        flavorText = "\"One faith, but many hands in victory.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6ebec82-9d4a-4e78-b923-37c3a52133e7.jpg?1783940736"
        ruling("2012-05-01", "The creature that entered will also get +1/+1 until end of turn if it's still on the battlefield when the ability resolves.")
    }
}
