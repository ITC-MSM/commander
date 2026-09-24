package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Inspired Tethermage — Reality Fracture #109
 * {2}{G} · Creature — Elf Warrior · 3/2
 *
 * Whenever you put one or more loyalty counters on a planeswalker, put a +1/+1 counter on this
 * creature.
 * {6}: Empower Jace 2.
 *
 * "You put" is the placer axis (CR 122.6), not the recipient's controller: it covers a [+N]
 * loyalty cost you pay (CR 606.4), a planeswalker entering under your control with its loyalty
 * (CR 122.6a), and effects you control — empower Jace included — whichever player's planeswalker
 * gets them. It fires once per planeswalker per placement, every time (no "first time each turn").
 */
val InspiredTethermage = card("Inspired Tethermage") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 3
    toughness = 2
    oracleText = "Whenever you put one or more loyalty counters on a planeswalker, put a +1/+1 counter " +
        "on this creature.\n" +
        "{6}: Empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't " +
        "control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and " +
        "\"[−3]: Draw a card.\")"

    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Planeswalker,
            counterType = Counters.LOYALTY,
            firstTimeEachTurn = false,
            placedBy = Player.You
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you put one or more loyalty counters on a planeswalker, put a +1/+1 " +
            "counter on this creature."
    }

    activatedAbility {
        cost = Costs.Mana("{6}")
        effect = Patterns.Mechanic.empowerJace(2)
        description = "Empower Jace 2."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Cristi Balanescu"
        imageUri = "https://cards.scryfall.io/normal/front/0/7/073f4998-a204-447b-93d5-746ae87fd6a1.jpg?1788878192"
        inBooster = false
    }
}
