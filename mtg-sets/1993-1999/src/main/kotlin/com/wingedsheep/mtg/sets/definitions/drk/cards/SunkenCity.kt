package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sunken City
 * {U}{U}
 * Enchantment
 * At the beginning of your upkeep, sacrifice this enchantment unless you pay {U}{U}.
 * Blue creatures get +1/+1.
 *
 * The upkeep tax is the Junún Efreet shape ([PayOrSufferEffect] + [SacrificeSelfEffect]); the
 * anthem is a plain [ModifyStats] static over every blue creature, whoever controls it.
 */
val SunkenCity = card("Sunken City") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, sacrifice this enchantment unless you pay {U}{U}.\n" +
        "Blue creatures get +1/+1."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = PayOrSufferEffect(
            cost = Costs.pay.Mana("{U}{U}"),
            suffer = SacrificeSelfEffect,
        )
    }

    staticAbility {
        ability = ModifyStats(1, 1, GroupFilter(GameObjectFilter.Creature.withColor(Color.BLUE)))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Jesper Myrfors"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1e0f9ec-2b06-4bda-8b80-a716d82d1f13.jpg?1783947942"
    }
}
