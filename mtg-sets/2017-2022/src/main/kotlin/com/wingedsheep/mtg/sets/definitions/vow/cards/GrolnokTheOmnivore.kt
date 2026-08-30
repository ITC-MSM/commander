package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayPlayCardsFromExile
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grolnok, the Omnivore
 * {2}{G}{U}
 * Legendary Creature — Frog
 * 3/3
 *
 * Whenever a Frog you control attacks, mill three cards.
 * Whenever a permanent card is put into your graveyard from your library, exile it with a croak
 * counter on it.
 * You may play lands and cast spells from among cards you own in exile with croak counters on them.
 *
 * Implementation notes:
 * - The attack trigger is [Triggers.attacks] with an `ANY` binding and a Frog-you-control filter, so
 *   it fires once per attacking Frog — Grolnok included, since it is itself a Frog.
 * - The second ability is a plain per-card `LIBRARY -> GRAVEYARD` [EventPattern.ZoneChangeEvent]
 *   rather than one of the batching `CardsPutIntoGraveyardFromLibraryEvent` specs. The printed text
 *   is singular ("a permanent card ... exile **it**"), so milling three permanents must put three
 *   triggers on the stack, each holding its own card as [EffectTarget.TriggeringEntity]. The batch
 *   spec would fire once and hand the payoff a collection, which is a different number of objects on
 *   the stack. Per its first ruling the trigger is *not* limited to cards Grolnok's own mill put
 *   there — any library-to-graveyard move qualifies — which is exactly what the unrestricted event
 *   gives.
 * - `fromZone = GRAVEYARD` makes the exile a no-op if something moved the card out of the graveyard
 *   in response (CR 400.7 — it would be a new object by then), and `addCounterType` stamps the croak
 *   counter as the card lands in exile.
 * - The play permission is a live [MayPlayCardsFromExile] filter over exile, not a set of remembered
 *   cards: per the second ruling it covers every croak-countered card you own "regardless of whether
 *   they were put there by the Grolnok that's currently on the battlefield or by an earlier
 *   incarnation". Cards in exile carry an owner but no controller, so `ownedByYou()` is the right
 *   predicate. Nothing is waived — all costs and normal timing rules still apply, which is what
 *   "play lands and cast spells" means (CR 601.3).
 */
val GrolnokTheOmnivore = card("Grolnok, the Omnivore") {
    manaCost = "{2}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Frog"
    power = 3
    toughness = 3
    oracleText = "Whenever a Frog you control attacks, mill three cards.\n" +
        "Whenever a permanent card is put into your graveyard from your library, exile it with a " +
        "croak counter on it.\n" +
        "You may play lands and cast spells from among cards you own in exile with croak counters " +
        "on them."

    // Whenever a Frog you control attacks, mill three cards.
    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.FROG).youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Library.mill(3)
    }

    // Whenever a permanent card is put into your graveyard from your library, exile it with a
    // croak counter on it.
    triggeredAbility {
        trigger = TriggerSpec(
            event = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Permanent.ownedByYou(),
                from = Zone.LIBRARY,
                to = Zone.GRAVEYARD,
            ),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Exile(
            target = EffectTarget.TriggeringEntity,
            fromZone = Zone.GRAVEYARD,
            addCounterType = CounterType.CROAK,
        )
    }

    // You may play lands and cast spells from among cards you own in exile with croak counters
    // on them.
    staticAbility {
        ability = MayPlayCardsFromExile(
            filter = GameObjectFilter.Any.ownedByYou().withCounter(Counters.CROAK),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "238"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7aaba76b-9cec-4c2b-b0eb-8f44201f6422.jpg?1783924793"

        ruling(
            "2021-11-19",
            "Grolnok, the Omnivore's second ability exiles all permanent cards that are put into " +
                "your graveyard from your library, not just those that you mill when a Frog you " +
                "control attacks."
        )
        ruling(
            "2021-11-19",
            "Grolnok's last ability allows you to play lands and cast spells from among all cards " +
                "you own in exile with croak counters on them, regardless of whether they were put " +
                "there by the Grolnok that's currently on the battlefield or by an earlier incarnation."
        )
    }
}
