package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Manticore of the Gauntlet
 * {4}{R}
 * Creature — Manticore
 * 5/4
 * When this creature enters, put a -1/-1 counter on target creature you control. This creature deals 3 damage to target opponent or planeswalker.
 *
 * One trigger, two targets: the counter's own creature and the damage's opponent-or-planeswalker,
 * both chosen as the ability goes on the stack.
 */
val ManticoreOfTheGauntlet = card("Manticore of the Gauntlet") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Manticore"
    oracleText = "When this creature enters, put a -1/-1 counter on target creature you control. This creature deals 3 damage to target opponent or planeswalker."
    power = 5
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.CreatureYouControl)
        val damaged = target("target 1", Targets.OpponentOrPlaneswalker)
        effect = Effects.Composite(
            Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, creature),
            Effects.DealDamage(3, damaged),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "James Paick"
        flavorText = "In the training ground known as the Gauntlet, initiates are pushed to practice more destructive techniques."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82b6ece3-1574-4634-b940-829e54c4f78d.jpg?1783936485"
    }
}
