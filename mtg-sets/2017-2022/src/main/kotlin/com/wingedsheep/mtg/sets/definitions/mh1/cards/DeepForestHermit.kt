package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deep Forest Hermit
 * {3}{G}{G}
 * Creature — Elf Druid
 * 1/1
 *
 * Vanishing 3
 * When this creature enters, create four 1/1 green Squirrel creature tokens.
 * Squirrels you control get +1/+1.
 *
 * Vanishing (CR 702.62) is spelled out from its three parts rather than declared as a keyword:
 * `Keyword.VANISHING` / `KeywordAbility.vanishing(n)` exist for rendering only — no engine
 * executor reads them — so a declared-only vanishing creature would enter with no time counters
 * and never be sacrificed. The three printed abilities are:
 *  - "enters with three time counters on it" → [EntersWithCounters] over [Counters.TIME]
 *  - "at the beginning of your upkeep, if it has a time counter on it, remove one" → an
 *    intervening-"if" upkeep trigger
 *  - "when the last is removed, sacrifice it" → [Triggers.countersRemovedFrom] with
 *    `lastRemoved = true` bound to the source
 *
 * "Squirrels you control" carries no card type, so it is every Squirrel *permanent* you control —
 * [GameObjectFilter.Permanent], not `.Creature`. The Hermit is an Elf Druid, so it never pumps
 * itself and no `excludeSelf` is needed.
 */
val DeepForestHermit = card("Deep Forest Hermit") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "Vanishing 3 (This creature enters with three time counters on it. At the " +
        "beginning of your upkeep, remove a time counter from it. When the last is removed, " +
        "sacrifice it.)\n" +
        "When this creature enters, create four 1/1 green Squirrel creature tokens.\n" +
        "Squirrels you control get +1/+1."
    power = 1
    toughness = 1

    // Vanishing 3, part 1 — enters with three time counters on it.
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.TIME),
            count = 3,
            selfOnly = true
        )
    )

    // Vanishing 3, part 2 — at the beginning of your upkeep, if it has a time counter on it,
    // remove a time counter from it.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Conditions.SourceCounterCountAtLeast(Counters.TIME, 1)
        effect = Effects.RemoveCounters(Counters.TIME, 1, EffectTarget.Self)
        description = "At the beginning of your upkeep, remove a time counter from this creature."
    }

    // Vanishing 3, part 3 — when the last time counter is removed, sacrifice it.
    triggeredAbility {
        trigger = Triggers.countersRemovedFrom(
            counterType = Counters.TIME,
            lastRemoved = true,
            binding = TriggerBinding.SELF
        )
        effect = Effects.SacrificeTarget(EffectTarget.Self)
        description = "When the last time counter is removed from this creature, sacrifice it."
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Squirrel"),
            count = 4
        )
    }

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.SQUIRREL).youControl()
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "161"
        artist = "Chris Seaman"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/3287775f-7bec-4e8f-bb8d-daf5ce92e4a8.jpg?1783933100"
    }
}
