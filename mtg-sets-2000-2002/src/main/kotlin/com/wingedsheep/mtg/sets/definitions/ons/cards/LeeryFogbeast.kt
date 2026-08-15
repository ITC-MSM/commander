package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Leery Fogbeast
 * {2}{G}
 * Creature — Beast
 * 4/2
 * Whenever Leery Fogbeast becomes blocked, prevent all combat damage that would be dealt this turn.
 */
val LeeryFogbeast = card("Leery Fogbeast") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 2
    oracleText = "Whenever Leery Fogbeast becomes blocked, prevent all combat damage that would be dealt this turn."

    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.PreventAllCombatDamage()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "273"
        artist = "Matt Cavotta"
        flavorText = "It emerges from the mists only to feed."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56125660-2307-4270-a947-f1f4ad63841c.jpg?1562915161"
    }
}
