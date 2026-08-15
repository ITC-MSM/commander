package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The One Ring
 * {4}
 * Legendary Artifact
 *
 * Indestructible
 * When The One Ring enters, if you cast it, you gain protection from everything until your next turn.
 * At the beginning of your upkeep, you lose 1 life for each burden counter on The One Ring.
 * {T}: Put a burden counter on The One Ring, then draw a card for each burden counter on The One Ring.
 *
 * Engine notes (Gap 8 — player-level protection):
 * - "You gain protection from everything until your next turn" is the player counterpart of the
 *   creature protection statics. Modeled via `Effects.GrantPlayerProtection()` (defaults to
 *   ProtectionScope.Everything / Duration.UntilYourNextTurn / the controller). While protected the
 *   controller can't be targeted by, or dealt damage from, any source (CR 702.16), until cleared
 *   after the untap step of their next turn.
 * - The ETB is gated on `Conditions.WasCast` ("if you cast it") — putting The One Ring onto the
 *   battlefield by another effect grants no protection (CR 603.4 intervening-if).
 * - The {T} ability adds the burden counter first, then draws reading the *new* count (sequential
 *   resolution, CR 608.2c): `AddCounters` then `DrawCards(countersOnSelf(burden))`.
 * - The burden counter has no rules meaning of its own; it only feeds the upkeep life-loss and the
 *   draw count, and grows the upkeep tax each activation.
 */
val TheOneRing = card("The One Ring") {
    manaCost = "{4}"
    typeLine = "Legendary Artifact"
    oracleText = "Indestructible\n" +
        "When The One Ring enters, if you cast it, you gain protection from everything until your next turn.\n" +
        "At the beginning of your upkeep, you lose 1 life for each burden counter on The One Ring.\n" +
        "{T}: Put a burden counter on The One Ring, then draw a card for each burden counter on The One Ring."

    keywords(Keyword.INDESTRUCTIBLE)

    // When The One Ring enters, if you cast it, you gain protection from everything until your next turn.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasCast
        effect = Effects.GrantPlayerProtection()
    }

    // At the beginning of your upkeep, you lose 1 life for each burden counter on The One Ring.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.LoseLife(
            DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.BURDEN)),
            EffectTarget.Controller
        )
    }

    // {T}: Put a burden counter on The One Ring, then draw a card for each burden counter on it.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Composite(
            listOf(
                Effects.AddCounters(Counters.BURDEN, 1, EffectTarget.Self),
                Effects.DrawCards(DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.BURDEN)))
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "246"
        artist = "Veli Nyström"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5806e68-1054-458e-866d-1f2470f682b2.jpg?1763472900"
        ruling(
            "2023-06-16",
            "If a player has protection from everything, it means three things: 1) All damage that " +
                "would be dealt to that player is prevented. 2) Auras can't be attached to that " +
                "player. 3) That player can't be the target of spells or abilities."
        )
        ruling(
            "2023-06-16",
            "Nothing other than the specified events are prevented or illegal. An effect that " +
                "doesn't target you could still cause you to discard cards, for example. Creatures " +
                "can still attack you while you have protection from everything, although combat " +
                "damage that they would deal to you will be prevented."
        )
        ruling(
            "2023-06-16",
            "Gaining protection from everything causes a spell or ability on the stack to have an " +
                "illegal target if it targets you. As a spell or ability tries to resolve, if all " +
                "its targets are illegal, that spell or ability doesn't resolve and none of its " +
                "effects happen, including effects unrelated to the target. If at least one target " +
                "is still legal, the spell or ability does as much as it can to the remaining legal " +
                "targets, and its other effects still happen."
        )
        ruling(
            "2023-06-16",
            "Protection from everything will usually prevent damage if it would be dealt to you, " +
                "but some damage can't be prevented. In this case, that damage reduces your life " +
                "total as normal."
        )
    }
}
