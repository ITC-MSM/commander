package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Canyon Crab
 * {1}{U}
 * Creature — Crab
 * 0/5
 *
 * {1}{U}: This creature gets +2/-2 until end of turn.
 * At the beginning of your end step, if you haven't cast a spell from your hand this turn,
 * draw a card, then discard a card.
 *
 * Intervening-if (CR 603.4 / Scryfall ruling 2024-04-12): the end-step ability checks
 * "haven't cast a spell from your hand this turn" both when it would trigger and again as it
 * resolves, so casting a spell after the trigger but before resolution still fizzles it.
 */
val CanyonCrab = card("Canyon Crab") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 0
    toughness = 5
    oracleText = "{1}{U}: This creature gets +2/-2 until end of turn.\n" +
        "At the beginning of your end step, if you haven't cast a spell from your hand this turn, " +
        "draw a card, then discard a card."

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        effect = Effects.ModifyStats(power = 2, toughness = -2, target = EffectTarget.Self)
        description = "{1}{U}: This creature gets +2/-2 until end of turn."
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.Not(Conditions.YouCastSpellsThisTurn(1, fromZone = Zone.HAND))
        effect = Patterns.Hand.loot(draw = 1, discard = 1)
        description = "At the beginning of your end step, if you haven't cast a spell from your hand " +
            "this turn, draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "40"
        artist = "Ignatius Budi"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b740a8a8-e1d3-4642-a214-03731c9b5553.jpg?1712355388"
    }
}
