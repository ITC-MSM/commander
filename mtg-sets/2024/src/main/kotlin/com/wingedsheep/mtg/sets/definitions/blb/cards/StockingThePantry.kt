package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stocking the Pantry
 * {G}
 * Enchantment
 *
 * Whenever you put one or more +1/+1 counters on a creature you control,
 * put a supply counter on this enchantment.
 * {2}, Remove a supply counter from this enchantment: Draw a card.
 */
val StockingThePantry = card("Stocking the Pantry") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever you put one or more +1/+1 counters on a creature you control, " +
        "put a supply counter on this enchantment.\n" +
        "{2}, Remove a supply counter from this enchantment: Draw a card."

    triggeredAbility {
        // "Whenever **you** put …" — CR 122.6 makes the placer part of the event, so this needs
        // `placedBy`. `Triggers.PlusOneCountersPlacedOnYourCreature` leaves it null, which is the
        // passive "are put on a creature you control" wording and fires on an opponent's placement
        // too. Found by the differential gate.
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Creature.youControl(),
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            firstTimeEachTurn = false,
            placedBy = Player.You,
        )
        effect = Effects.AddCounters(Counters.SUPPLY, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.RemoveCounterFromSelf(Counters.SUPPLY)
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "194"
        artist = "Gina Matarazzo"
        flavorText = "Sorting the seeds from the nuts\n—Squirrelfolk expression meaning\n\"dwelling on unnecessary details\""
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50e95c7b-f0b2-4276-8c5e-4191b7ba35d1.jpg?1727185660"
    }
}
