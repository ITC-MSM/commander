package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.MayCastSelfFromZones
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Hundred-Battle Veteran
 * {3}{B}
 * Creature — Zombie Warrior
 * 4/2
 * As long as there are three or more different kinds of counters among creatures you control,
 * this creature gets +2/+4.
 * You may cast this card from your graveyard. If you do, it enters with a finality counter on it.
 * (If a creature with a finality counter on it would die, exile it instead.)
 */
val HundredBattleVeteran = card("Hundred-Battle Veteran") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Warrior"
    power = 4
    toughness = 2
    oracleText = "As long as there are three or more different kinds of counters among creatures " +
        "you control, this creature gets +2/+4.\n" +
        "You may cast this card from your graveyard. If you do, it enters with a finality counter on it. " +
        "(If a creature with a finality counter on it would die, exile it instead.)"

    // +2/+4 while three or more different kinds of counters are among creatures you control.
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 4, filter = GroupFilter.source()),
            condition = Conditions.DifferentCounterKindsAtLeast(3)
        )
    }

    // You may cast this card from your graveyard...
    staticAbility {
        ability = MayCastSelfFromZones(zones = listOf(Zone.GRAVEYARD))
    }

    // ...If you do, it enters with a finality counter on it.
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.FINALITY),
            count = 1,
            selfOnly = true,
            condition = Conditions.WasCastFromGraveyard
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Wayne Wu"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e53adf93-2db5-4087-a2dc-c8f53401d700.jpg?1743204289"
    }
}
