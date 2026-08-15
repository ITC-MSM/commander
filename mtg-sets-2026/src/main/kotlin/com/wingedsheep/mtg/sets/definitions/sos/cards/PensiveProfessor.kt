package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.increment
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.CountersPlacedEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec

/**
 * Pensive Professor — Secrets of Strixhaven #63
 * {1}{U}{U} · Creature — Human Wizard · 0/2
 *
 * Increment (Whenever you cast a spell, if the amount of mana you spent is greater than this
 *   creature's power or toughness, put a +1/+1 counter on this creature.)
 * Whenever one or more +1/+1 counters are put on this creature, draw a card.
 *
 * Increment is supplied by the [increment] ability-word builder (already in the engine). The draw
 * payoff fires on a [CountersPlacedEvent] bound to this creature ([TriggerBinding.SELF]); unlike
 * Exemplar of Light there is no "once each turn" restriction, so the trigger has no `oncePerTurn`.
 */
val PensiveProfessor = card("Pensive Professor") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 0
    toughness = 2
    oracleText = "Increment (Whenever you cast a spell, if the amount of mana you spent is greater than this creature's power or toughness, put a +1/+1 counter on this creature.)\n" +
        "Whenever one or more +1/+1 counters are put on this creature, draw a card."

    increment()

    // Whenever one or more +1/+1 counters are put on this creature, draw a card.
    triggeredAbility {
        trigger = TriggerSpec(
            event = CountersPlacedEvent(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                filter = GameObjectFilter.Any,
            ),
            binding = TriggerBinding.SELF,
        )
        effect = Effects.DrawCards(1)
        description = "Whenever one or more +1/+1 counters are put on this creature, draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "63"
        artist = "Billy Christian"
        flavorText = "\"To the patient mind, no problem is unsolvable.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66d47940-84f9-4479-8562-45e5148435d4.jpg?1775937349"
    }
}
