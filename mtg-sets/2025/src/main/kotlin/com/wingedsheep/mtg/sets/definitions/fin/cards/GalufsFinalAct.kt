package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Galuf's Final Act
 * {1}{G}
 * Instant
 *
 * Until end of turn, target creature gets +1/+0 and gains "When this creature dies,
 * put a number of +1/+1 counters equal to its power on up to one target creature."
 *
 * Composition: a +1/+0 pump until end of turn ([Effects.ModifyStats], EOT) plus a granted
 * self [Triggers.Dies] trigger ([GrantTriggeredAbilityEffect], EOT — mirrors the in-set
 * Vincent's Limit Break "dies → return tapped" grant). The granted ability's effect puts a
 * dynamic number of +1/+1 counters ([Effects.AddDynamicCounters]) equal to the dying
 * creature's power on up to one target creature.
 *
 * "Its power" reads the dying creature's *last-known* power via [DynamicAmounts.sourcePower]
 * (the granted ability's source is the creature it was granted to). The same
 * `EntityProperty(Source, Power)` resolution backs Heartfire Hero's "deals damage equal to
 * its power" dies trigger — the DynamicAmountEvaluator falls back to the leave-battlefield
 * power snapshot once the source is in the graveyard, so the printed base power is never used.
 *
 * "Up to one target creature" is an optional (0-or-1) target requirement
 * ([Targets.UpToCreatures] with `count = 1`); declining resolves with no counters placed.
 */
val GalufsFinalAct = card("Galuf's Final Act") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Until end of turn, target creature gets +1/+0 and gains \"When this " +
        "creature dies, put a number of +1/+1 counters equal to its power on up to one " +
        "target creature.\""

    spell {
        val t = target("target", Targets.Creature)

        val diesGrantCounters = TriggeredAbility.create(
            trigger = Triggers.Dies.event,
            binding = Triggers.Dies.binding,
            effect = Effects.AddDynamicCounters(
                Counters.PLUS_ONE_PLUS_ONE,
                DynamicAmounts.sourcePower(),
                EffectTarget.ContextTarget(0)
            ),
            targetRequirement = Targets.UpToCreatures(1),
            descriptionOverride = "When this creature dies, put a number of +1/+1 counters " +
                "equal to its power on up to one target creature."
        )

        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, t, Duration.EndOfTurn),
            GrantTriggeredAbilityEffect(diesGrantCounters, t, Duration.EndOfTurn)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "186"
        artist = "Nijihayashi"
        flavorText = "\"I'll destroy you Exdeath... even if it means I have to take you " +
            "into the afterlife myself!\""
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0ce05634-6c01-4941-a135-904cb4e33ac4.jpg?1748706456"
    }
}
