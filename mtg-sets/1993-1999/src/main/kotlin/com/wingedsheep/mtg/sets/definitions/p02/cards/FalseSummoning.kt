package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * False Summoning
 * {1}{U}
 * Instant
 * Counter target creature spell.
 *
 * [Targets.CreatureSpell] is the stack-zoned creature filter; [Effects.CounterSpell] counters
 * whatever the requirement bound, so the effect carries no target of its own.
 */
val FalseSummoning = card("False Summoning") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target creature spell."

    spell {
        target("target", Targets.CreatureSpell)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "DiTerlizzi"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd7d30a8-bc7a-42bc-8d1b-600cbf78ab98.jpg"
    }
}
