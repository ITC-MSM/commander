package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * First Day of Class — Strixhaven: School of Mages #102 (canonical printing)
 * {1}{R} · Instant
 *
 * Whenever a creature you control enters this turn, put a +1/+1 counter on it and it gains haste
 * until end of turn.
 * Learn.
 *
 * An instant that installs a *turn-bounded, filter-scoped* delayed triggered ability — the same
 * shape as Thunder of Unity's chapters II/III. `fireOnce = false` because it fires for **every**
 * creature you control that enters this turn, not just the first, and
 * `expiry = DelayedTriggerExpiry.EndOfTurn` is the printed "this turn".
 *
 * Scoping is by [GameObjectFilter] rather than a watched entity — there is no single permanent to
 * watch, and the filter's controller predicate is what keeps it from firing on an opponent's
 * creatures. [EffectTarget.TriggeringEntity] inside the trigger is the creature that just entered,
 * so "it" lands on the right permanent.
 *
 * Cast before combat on an empty board this does nothing at all; the Learn is what stops it from
 * being a blank card in that spot.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val FirstDayOfClass = card("First Day of Class") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Whenever a creature you control enters this turn, put a +1/+1 counter on it and " +
        "it gains haste until end of turn.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        effect = CreateDelayedTriggerEffect(
            trigger = Triggers.entersBattlefield(
                filter = GameObjectFilter.Creature.youControl(),
                binding = TriggerBinding.ANY
            ),
            fireOnce = false,
            expiry = DelayedTriggerExpiry.EndOfTurn,
            effect = Effects.AddCounters(
                Counters.PLUS_ONE_PLUS_ONE,
                1,
                EffectTarget.TriggeringEntity
            ) then Effects.GrantKeyword(Keyword.HASTE, EffectTarget.TriggeringEntity)
        ) then Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Paul Scott Canavan"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/091eb13d-9318-4b12-9f94-6276b11981d1.jpg?1783927356"
    }
}
