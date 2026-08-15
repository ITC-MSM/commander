package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec

/**
 * Morbid Opportunist
 * {2}{B}
 * Creature — Human Rogue
 * 1/3
 * Whenever one or more other creatures die, draw a card. This ability triggers only once each turn.
 *
 * A batched death trigger (CR 603.3b): it fires at most once per death batch regardless of how many
 * creatures died, and `oncePerTurn` caps it to a single draw per turn. The batch is scoped to every
 * player's creatures (`anyController`) and excludes the source's own death (`excludeSelf`) to honor
 * the "one or more *other* creatures" wording — this is the [Triggers.OneOrMoreCreaturesDie] shape
 * with `excludeSelf` set, which the facade doesn't yet expose, so the [TriggerSpec] is built inline.
 */
val MorbidOpportunist = card("Morbid Opportunist") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    power = 1
    toughness = 3
    oracleText = "Whenever one or more other creatures die, draw a card. This ability triggers only once each turn."

    triggeredAbility {
        trigger = TriggerSpec(
            event = EventPattern.CreaturesYouControlDiedEvent(
                filter = GameObjectFilter.Creature.anyController(),
                excludeSelf = true
            ),
            binding = TriggerBinding.ANY
        )
        oncePerTurn = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "113"
        artist = "Tyler Walpole"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a53982e-3d66-4808-bcb5-46ff40567872.jpg?1782703660"
    }
}
