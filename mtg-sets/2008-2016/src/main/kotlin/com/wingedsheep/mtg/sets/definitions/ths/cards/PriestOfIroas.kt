package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Priest of Iroas
 * {R}
 * Creature — Human Cleric
 * 1 / 1
 *
 * {3}{W}, Sacrifice this creature: Destroy target enchantment.
 */
val PriestOfIroas = card("Priest of Iroas") {
    manaCost = "{R}"
    colorIdentity = "WR"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "{3}{W}, Sacrifice this creature: Destroy target enchantment."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}"), Costs.SacrificeSelf)
        val t = target("target", Targets.Enchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Clint Cearley"
        flavorText = "\"Even my last breath will be a blow struck for Iroas.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/1/013ec9f5-8bf3-4067-a942-d535d011af82.jpg"
    }
}
