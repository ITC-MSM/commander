package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Teferi's Care
 * {2}{W}
 * Enchantment
 * {W}, Sacrifice an enchantment: Destroy target enchantment.
 * {3}{U}{U}: Counter target enchantment spell.
 */
val TeferisCare = card("Teferi's Care") {
    manaCost = "{2}{W}"
    colorIdentity = "WU"
    typeLine = "Enchantment"
    oracleText = "{W}, Sacrifice an enchantment: Destroy target enchantment.\n" +
        "{3}{U}{U}: Counter target enchantment spell."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Sacrifice(GameObjectFilter.Enchantment))
        val t = target("enchantment", Targets.Enchantment)
        effect = Effects.Destroy(t)
    }

    activatedAbility {
        cost = Costs.Mana("{3}{U}{U}")
        target = TargetSpell(filter = TargetFilter(GameObjectFilter.Enchantment, zone = Zone.STACK))
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Scott Bailey"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/031b1cc1-4468-4bc5-85c0-c22dce131225.jpg?1562895604"
    }
}
