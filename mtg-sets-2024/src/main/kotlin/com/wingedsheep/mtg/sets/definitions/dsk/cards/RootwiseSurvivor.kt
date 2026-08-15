package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Rootwise Survivor
 * {3}{G}{G}
 * Creature — Human Survivor
 * 3/4
 *
 * Haste
 * Survival — At the beginning of your second main phase, if this creature is tapped, put
 * three +1/+1 counters on up to one target land you control. That land becomes a 0/0
 * Elemental creature in addition to its other types. It gains haste until your next turn.
 *
 * "Survival" is an ability word (no rules meaning) — modeled as a postcombat-main-phase
 * trigger ([Triggers.YourPostcombatMain]) with an intervening-if ([Conditions.SourceIsTapped],
 * CR 603.4 — checked both when it would trigger and on resolution). The target is "up to one"
 * land you control (`optional = true`), so the ability still resolves with no target chosen.
 * The animation is permanent — the land stays a 0/0 Elemental creature (in addition to its
 * land type) with the three +1/+1 counters; only the granted haste is bounded to
 * [Duration.UntilYourNextTurn].
 */
val RootwiseSurvivor = card("Rootwise Survivor") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Survivor"
    power = 3
    toughness = 4
    oracleText = "Haste\n" +
        "Survival — At the beginning of your second main phase, if this creature is tapped, " +
        "put three +1/+1 counters on up to one target land you control. That land becomes a " +
        "0/0 Elemental creature in addition to its other types. It gains haste until your next turn."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl(), optional = true))
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, land),
            Effects.BecomeCreature(
                target = land,
                power = 0,
                toughness = 0,
                creatureTypes = setOf("Elemental"),
                duration = Duration.Permanent,
            ),
            Effects.GrantKeyword(Keyword.HASTE, land, Duration.UntilYourNextTurn),
        )
        description = "Survival — At the beginning of your second main phase, if this creature " +
            "is tapped, put three +1/+1 counters on up to one target land you control. That land " +
            "becomes a 0/0 Elemental creature in addition to its other types. It gains haste " +
            "until your next turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "196"
        artist = "Joseph Weston"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d843c242-088f-4131-9e52-7ee2d0db5e20.jpg?1726286598"
    }
}
