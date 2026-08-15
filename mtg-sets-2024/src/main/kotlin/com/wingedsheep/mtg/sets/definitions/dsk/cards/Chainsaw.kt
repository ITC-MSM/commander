package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Chainsaw
 * {1}{R}
 * Artifact — Equipment
 * When this Equipment enters, it deals 3 damage to up to one target creature.
 * Whenever one or more creatures die, put a rev counter on this Equipment.
 * Equipped creature gets +X/+0, where X is the number of rev counters on this Equipment.
 * Equip {3}
 *
 * The "whenever one or more creatures die" trigger is the batched death shape
 * ([Triggers.OneOrMoreCreaturesDie]): it fires at most once per death batch regardless of how many
 * creatures died simultaneously and regardless of who controlled them (CR 603.3b), so a board wipe
 * adds exactly one rev counter, not one per creature.
 *
 * Rev counters are passive storage counters (no inherent rule); the +X/+0 static reads the count off
 * the Equipment (the source) via [DynamicAmounts.countersOnSelf] and applies it to the equipped
 * creature ([Filters.EquippedCreature]).
 */
val Chainsaw = card("Chainsaw") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, it deals 3 damage to up to one target creature.\n" +
        "Whenever one or more creatures die, put a rev counter on this Equipment.\n" +
        "Equipped creature gets +X/+0, where X is the number of rev counters on this Equipment.\n" +
        "Equip {3}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("up to one target creature", TargetCreature(optional = true, filter = TargetFilter.Creature))
        effect = Effects.DealDamage(3, t)
    }

    triggeredAbility {
        trigger = Triggers.OneOrMoreCreaturesDie()
        effect = Effects.AddCounters(Counters.REV, 1, EffectTarget.Self)
    }

    staticAbility {
        val revCount = DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.REV))
        ability = GrantDynamicStatsEffect(
            filter = Filters.EquippedCreature,
            powerBonus = revCount,
            toughnessBonus = DynamicAmount.Fixed(0)
        )
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "128"
        artist = "J.P. Targete"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54e0c2cd-fa5f-427d-8e15-6066f002a8e3.jpg?1726286325"
    }
}
