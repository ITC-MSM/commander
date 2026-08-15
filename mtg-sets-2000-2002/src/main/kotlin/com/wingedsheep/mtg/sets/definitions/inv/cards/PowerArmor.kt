package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Power Armor
 * {4}
 * Artifact
 * Domain — {3}, {T}: Target creature gets +1/+1 until end of turn for each basic land
 * type among lands you control.
 */
val PowerArmor = card("Power Armor") {
    manaCost = "{4}"
    typeLine = "Artifact"
    oracleText = "Domain — {3}, {T}: Target creature gets +1/+1 until end of turn for each basic land type among lands you control."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(DynamicAmounts.domain(), DynamicAmounts.domain(), t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "309"
        artist = "Doug Chaffee"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed1981dd-c0f3-4e9d-a1f1-8bea823326ef.jpg?1562942628"
    }
}
