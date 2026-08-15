package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Undergrowth Leopard
 * {1}{G}
 * Creature — Cat
 * 2/2
 *
 * Vigilance
 * {1}, Sacrifice this creature: Destroy target artifact or enchantment.
 */
val UndergrowthLeopard = card("Undergrowth Leopard") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    power = 2
    toughness = 2
    oracleText = "Vigilance\n" +
        "{1}, Sacrifice this creature: Destroy target artifact or enchantment."

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Iris Compiet"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67ab8f9a-b17c-452f-b4ef-a3f91909e3de.jpg?1743204627"
    }
}
