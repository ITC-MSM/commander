package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Iron Bully
 * {3}
 * Artifact Creature — Golem
 * 1/1
 * Menace (This creature can't be blocked except by two or more creatures.)
 * When this creature enters, put a +1/+1 counter on target creature.
 *
 * The ETB targets any creature — including Iron Bully itself, per the ruling — so the target
 * requirement is the unrestricted [Targets.Creature], not the "you control" variant.
 */
val IronBully = card("Iron Bully") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 1
    toughness = 1
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\nWhen this creature enters, put a +1/+1 counter on target creature."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        description = "When this creature enters, put a +1/+1 counter on target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "240"
        artist = "Aaron Miller"
        flavorText = "\"Why would someone have built ... wait, never mind. Send it to the front lines!\" —Commander Grozdan"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/ded2c66e-402c-4d5c-b987-402679aa914b.jpg?1783933373"
        ruling("2020-08-07", "Iron Bully can be the target of its own ability.")
    }
}
