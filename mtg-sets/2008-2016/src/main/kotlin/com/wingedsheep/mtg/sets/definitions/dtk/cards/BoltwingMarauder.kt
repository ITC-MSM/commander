package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Boltwing Marauder
 * {3}{B}{R}
 * Creature — Dragon
 * 5 / 4
 *
 * Flying
 * Whenever another creature you control enters, target creature gets +2/+0 until end of turn.
 *
 * "**Another** creature you control" is the binding, not the filter: [Triggers.OtherCreatureEnters]
 * is `Creature.youControl()` entering the battlefield under [com.wingedsheep.sdk.scripting.TriggerBinding.OTHER],
 * so the Dragon's own arrival doesn't pump anything. The pump names its own target — "target
 * creature", any creature on the battlefield, not just yours — so the ability declares its
 * own [Targets.Creature] requirement rather than reusing the triggering permanent.
 */
val BoltwingMarauder = card("Boltwing Marauder") {
    manaCost = "{3}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Dragon"
    power = 5
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever another creature you control enters, target creature gets +2/+0 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "214"
        artist = "Raymond Swanland"
        flavorText = "When battling the Kolaghan, consider yourself lucky if lightning strikes the same place only twice."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aab8841f-5c6f-47fc-91c9-acf3c84b7313.jpg?1783938573"
    }
}
