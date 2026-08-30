package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CounterRemovalAmount
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.PreventDamageByRemovingCounter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Magma Pummeler
 * {X}{R}{R}
 * Creature — Elemental
 * 0/0
 *
 * This creature enters with X +1/+1 counters on it.
 * If damage would be dealt to this creature while it has a +1/+1 counter on it, prevent that
 * damage and remove that many +1/+1 counters from it. When one or more counters are removed from
 * this creature this way, it deals that much damage to any target.
 *
 * The second line inverts *both* of [PreventDamageByRemovingCounter]'s printed defaults, which is
 * the whole reason it needed new vocabulary:
 *
 *  - **`requiresCounter = true`** — "while it has a +1/+1 counter on it". Unbreathing Horde
 *    prevents damage whether or not it has a counter left; the Pummeler with no counters is a 0/0
 *    that is already dead, and any damage aimed at it is dealt normally.
 *  - **`removalAmount = EqualToDamage`** — "remove *that many*", where Unbreathing Horde's rule is
 *    exactly one counter per damage event however large the damage.
 *
 * The bound matters and the ruling pins it: damage greater than the counter count is *all*
 * prevented and *all* the counters go. So a 3/3 Pummeler taking 5 prevents 5, loses 3 counters, and
 * deals **3** — "that much" is the number of counters actually removed, not the damage. That is why
 * the trigger reads [ContextPropertyKey.TRIGGER_COUNTERS_REMOVED_AMOUNT] off the removal event
 * rather than the damage.
 *
 * The trigger is scoped with `byDamagePrevention = true` — the printed "**this way**". Without it
 * the Pummeler would also ping when a +1/+1 counter left it as a cost or to an opponent's effect.
 *
 * Losing its last counters usually leaves a 0/0 that dies to the toughness state-based action. The
 * trigger has already been put on the stack by then and still resolves, per the ruling.
 */
val MagmaPummeler = card("Magma Pummeler") {
    manaCost = "{X}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 0
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it.\n" +
        "If damage would be dealt to this creature while it has a +1/+1 counter on it, prevent " +
        "that damage and remove that many +1/+1 counters from it. When one or more counters are " +
        "removed from this creature this way, it deals that much damage to any target."

    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    replacementEffect(
        PreventDamageByRemovingCounter(
            removalAmount = CounterRemovalAmount.EqualToDamage,
            requiresCounter = true
        )
    )

    triggeredAbility {
        trigger = Triggers.countersRemovedFrom(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            byDamagePrevention = true,
            binding = TriggerBinding.SELF
        )
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(
            DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_COUNTERS_REMOVED_AMOUNT),
            anyTarget
        )
        description = "When one or more counters are removed from this creature this way, it " +
            "deals that much damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "169"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42f133d6-35c3-49b7-85f6-d6cfa6bac3d9.jpg?1783924830"
        ruling("2021-11-19", "If an amount of damage would be dealt to Magma Pummeler greater than the number of +1/+1 counters on it, all of that damage is prevented, and all of those counters are removed. In most cases, this will result in Magma Pummeler having 0 toughness, so it will be put into its owner's graveyard. Its reflexive triggered ability still triggers and it will still deal that much damage to any target.")
    }
}
