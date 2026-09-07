package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Necroplasm
 * {1}{B}{B}
 * Creature — Ooze
 * 1/1
 * At the beginning of your upkeep, put a +1/+1 counter on this creature.
 * At the beginning of your end step, destroy each creature with mana value equal to the number of
 * +1/+1 counters on this creature.
 * Dredge 2
 *
 * A sweeper on a clock: the counter added each upkeep is also the mana value the end step sweeps,
 * so it eats one-drops on the turn it lands, two-drops next turn, and so on up the curve.
 *
 * The destroy filter reads the counter count off the source at resolution
 * (`manaValueEqualsDynamic` over an [EntityNumericProperty.CounterCount] on
 * [EntityReference.Source]) rather than baking a number in — the whole point of the card is that
 * the number moves. Necroplasm's own mana value is 3, so a third counter includes it in its own
 * sweep; nothing special is needed for that, it simply matches its own filter.
 *
 * Dredge 2 is what makes the escalation reusable: the Ooze that swept itself away comes back for
 * a draw and starts the count over at one.
 */
val Necroplasm = card("Necroplasm") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Ooze"
    power = 1
    toughness = 1
    oracleText = "At the beginning of your upkeep, put a +1/+1 counter on this creature.\n" +
        "At the beginning of your end step, destroy each creature with mana value equal to the number of +1/+1 counters on this creature.\n" +
        "Dredge 2 (If you would draw a card, you may mill two cards instead. If you do, return this card from your graveyard to your hand.)"

    // "At the beginning of your upkeep, put a +1/+1 counter on this creature."
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "At the beginning of your upkeep, put a +1/+1 counter on this creature."
    }

    // "At the beginning of your end step, destroy each creature with mana value equal to the
    //  number of +1/+1 counters on this creature."
    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.DestroyAll(
            GameObjectFilter.Creature.manaValueEqualsDynamic(
                DynamicAmount.EntityProperty(
                    EntityReference.Source,
                    EntityNumericProperty.CounterCount(CounterTypeFilter.PlusOnePlusOne)
                )
            )
        )
        description = "At the beginning of your end step, destroy each creature with mana value " +
            "equal to the number of +1/+1 counters on this creature."
    }

    keywordAbility(KeywordAbility.dredge(2))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "98"
        artist = "rk post"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/372424cf-85b3-4a78-bcf2-0543b0b88c0b.jpg?1783943665"
        ruling(
            "2024-01-12",
            "You can't attempt to use a dredge ability if you don't have enough cards in your library."
        )
        ruling(
            "2024-01-12",
            "Dredge can replace any card draw, not only the one during your draw step."
        )
    }
}
