package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Fires of Yavimaya
 * {1}{R}{G}
 * Enchantment
 *
 * Creatures you control have haste.
 * Sacrifice this enchantment: Target creature gets +2/+2 until end of turn.
 */
val FiresOfYavimaya = card("Fires of Yavimaya") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Enchantment"
    oracleText = "Creatures you control have haste.\n" +
        "Sacrifice this enchantment: Target creature gets +2/+2 until end of turn."

    // Creatures you control have haste.
    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.HASTE,
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    // Sacrifice this enchantment: Target creature gets +2/+2 until end of turn.
    activatedAbility {
        cost = Costs.SacrificeSelf
        val t = target("target", Targets.Creature)
        effect = ModifyStatsEffect(
            powerModifier = 2,
            toughnessModifier = 2,
            target = t,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Val Mayerik"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/967f1658-8777-46fc-a648-07fb19e46745.jpg?1562925325"
    }
}
