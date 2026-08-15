package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Anim Pakal, Thousandth Moon
 * {1}{R}{W}
 * Legendary Creature — Human Soldier
 * 1/2
 *
 * Whenever you attack with one or more non-Gnome creatures, put a +1/+1 counter on Anim Pakal,
 * then create X 1/1 colorless Gnome artifact creature tokens that are tapped and attacking, where
 * X is the number of +1/+1 counters on Anim Pakal.
 *
 * Implementation notes:
 * - Trigger: [Triggers.YouAttackWithFilter] over [GameObjectFilter.Creature.notSubtype(Subtype.GNOME)]
 *   fires whenever you attack with at least one non-Gnome creature. Anim Pakal need not be
 *   attacking herself, but since she is a Human Soldier (not a Gnome) her presence in the attack
 *   alone satisfies the condition.
 * - The effect is a sequential [Effects.Composite]:
 *   1. [Effects.AddCounters] puts a +1/+1 counter on Anim Pakal ([EffectTarget.Self]).
 *   2. [CreateTokenEffect] reads [DynamicAmounts.countersOnSelf] with [CounterTypeFilter.PlusOnePlusOne]
 *      at resolution time — after step 1 has already incremented the count — so the token count
 *      always equals the total +1/+1 counters on Anim Pakal at the moment tokens are created.
 * - Tokens are 1/1 colorless Gnome artifact creature tokens: no color set, `artifactToken = true`,
 *   `creatureTypes = setOf("Gnome")`, `tapped = true`, `attacking = true`.
 * - The Gnome token uses the LCI Gnome token art (Scryfall set `tlci`, id 6def709a).
 */
val AnimPakal = card("Anim Pakal, Thousandth Moon") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "Whenever you attack with one or more non-Gnome creatures, put a +1/+1 counter on Anim Pakal, then create X 1/1 colorless Gnome artifact creature tokens that are tapped and attacking, where X is the number of +1/+1 counters on Anim Pakal."

    triggeredAbility {
        trigger = Triggers.YouAttackWithFilter(GameObjectFilter.Creature.notSubtype(Subtype.GNOME))
        effect = Effects.Composite(
            // Step 1: put a +1/+1 counter on Anim Pakal.
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            // Step 2: create X tapped-and-attacking 1/1 colorless Gnome artifact creature tokens,
            // where X = +1/+1 counters on Anim Pakal — evaluated after step 1.
            CreateTokenEffect(
                count = DynamicAmounts.countersOnSelf(CounterTypeFilter.PlusOnePlusOne),
                power = 1,
                toughness = 1,
                colors = emptySet(),
                creatureTypes = setOf("Gnome"),
                tapped = true,
                attacking = true,
                artifactToken = true,
                imageUri = "https://cards.scryfall.io/normal/front/6/d/6def709a-53b3-4520-9544-74ab6472d256.jpg?1783913604",
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "223"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/868856b7-8875-43c1-8249-0f8fb2c8319b.jpg?1782694432"
    }
}
