package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Assault Team — War of the Spark #129 (canonical printing)
 * {3}{R}
 * Creature — Goblin Warrior
 * 4/1
 * Haste
 * When this creature dies, put a +1/+1 counter on target creature you control.
 *
 * A dies trigger that targets: the counter goes onto a creature that is *still* on the
 * battlefield, so the target is chosen as the ability goes on the stack, after the team is
 * already in the graveyard. [Triggers.Dies] binds to the source, and the surviving creature is
 * the ability's own target.
 */
val GoblinAssaultTeam = card("Goblin Assault Team") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    oracleText = "Haste\n" +
        "When this creature dies, put a +1/+1 counter on target creature you control."
    power = 4
    toughness = 1

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.Dies
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Zoltan Boros"
        flavorText = "Under the veneer of the various guilds, each goblin has the same basic need: to run screaming at the enemy and hit a bunch of stuff."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/880047fd-d258-40fb-bcd5-37cb26678dfe.jpg"
    }
}
