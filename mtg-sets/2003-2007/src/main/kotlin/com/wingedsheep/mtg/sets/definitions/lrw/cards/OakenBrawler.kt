package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Oaken Brawler
 * {3}{W}
 * Creature — Treefolk Warrior
 * 2/4
 * When this creature enters, clash with an opponent. If you win, put a +1/+1 counter on this
 * creature.
 */
val OakenBrawler = card("Oaken Brawler") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Treefolk Warrior"
    power = 2
    toughness = 4
    oracleText = "When this creature enters, clash with an opponent. If you win, put a +1/+1 counter on " +
        "this creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.clash(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
        description = "clash with an opponent. If you win, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Jim Murray"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9fdb060-8f4d-453a-9516-92c390fbc85a.jpg?1783942911"
    }
}
