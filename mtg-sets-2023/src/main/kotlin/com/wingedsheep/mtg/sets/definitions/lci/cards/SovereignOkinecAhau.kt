package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Sovereign Okinec Ahau — The Lost Caverns of Ixalan #240 (canonical printing)
 * {2}{G}{W} · Legendary Creature — Cat Noble · 3/4
 *
 * Ward {2}
 * Whenever Sovereign Okinec Ahau attacks, for each creature you control with power greater than
 * that creature's base power, put a number of +1/+1 counters on that creature equal to the
 * difference.
 *
 * Composed entirely from existing primitives — no bespoke effect:
 *  - **Ward {2}** — `KeywordAbility.Ward(WardCost.Mana("{2}"))`, the standard mana-ward idiom.
 *  - **The attack payoff** is `Triggers.Attacks` (SELF) driving an [Effects.ForEachInGroup] over
 *    `GroupFilter(GameObjectFilter.Creature.youControl().powerGreaterThanBase())` — the same
 *    self-relative `PowerGreaterThanBase` filter the Malamet cycle (Kutzil, Malamet Exemplar) uses,
 *    so a creature qualifies exactly when its current (projected) power exceeds its printed base.
 *    The group is snapshotted before any counters land, and the Sovereign itself is a member
 *    (it qualifies only if it is itself pumped above 3 power).
 *  - **The per-creature amount** is the "difference" the oracle names:
 *    `Subtract(EntityProperty(IterationEntity, Power), EntityProperty(IterationEntity, BasePower))`
 *    — current projected power minus that same creature's printed base power. `IterationEntity`
 *    and `EffectTarget.Self` both resolve to the creature currently being processed, so each
 *    qualifying creature receives counters equal to its *own* excess (measured at resolution). The
 *    `BasePower` numeric-property read matches the filter's base exactly, so the counter count is
 *    always ≥ 1 for every group member; a creature at power == base is never in the group, and a
 *    `*`/CDA-power creature (no fixed base) is excluded by the filter.
 */
val SovereignOkinecAhau = card("Sovereign Okinec Ahau") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Cat Noble"
    power = 3
    toughness = 4
    oracleText = "Ward {2}\n" +
        "Whenever Sovereign Okinec Ahau attacks, for each creature you control with power greater " +
        "than that creature's base power, put a number of +1/+1 counters on that creature equal to " +
        "the difference."

    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{2}")))

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl().powerGreaterThanBase()),
            effect = Effects.AddDynamicCounters(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                amount = DynamicAmount.Subtract(
                    DynamicAmount.EntityProperty(EntityReference.IterationEntity, EntityNumericProperty.Power),
                    DynamicAmount.EntityProperty(EntityReference.IterationEntity, EntityNumericProperty.BasePower),
                ),
                target = EffectTarget.Self,
            ),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "240"
        artist = "Victor Adame Minguez"
        flavorText = "As surface empires send their explorers down, his hungry eyes look upward."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70c75aa7-e2f9-4a69-8086-c982019ca714.jpg?1782694418"
    }
}
