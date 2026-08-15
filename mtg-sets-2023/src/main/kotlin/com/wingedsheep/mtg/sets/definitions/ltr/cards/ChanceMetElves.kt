package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Chance-Met Elves
 * {2}{G}
 * Creature — Elf Warrior
 * 3/2
 *
 * Whenever you scry, put a +1/+1 counter on this creature. This ability
 * triggers only once each turn.
 */
val ChanceMetElves = card("Chance-Met Elves") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 3
    toughness = 2
    oracleText = "Whenever you scry, put a +1/+1 counter on this creature. " +
        "This ability triggers only once each turn."

    triggeredAbility {
        trigger = Triggers.WheneverYouScry
        oncePerTurn = true
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Irvin Rodriguez"
        flavorText = "\"It is said, 'Go not to the Elves for counsel, for they will say both no and yes.'\"\n—Frodo"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0aa57431-39e2-4152-8a30-1c1e8faef153.jpg?1686969269"
    }
}
