package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.TargetSharesMostCommonColor
import com.wingedsheep.sdk.scripting.effects.CantBeRegeneratedEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Tsabo's Assassin
 * {2}{B}{B}
 * Creature — Phyrexian Zombie Assassin
 * 1/1
 * {T}: Destroy target creature if it shares a color with the most common color among all
 * permanents or a color tied for most common. A creature destroyed this way can't be regenerated.
 */
val TsabosAssassin = card("Tsabo's Assassin") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Zombie Assassin"
    power = 1
    toughness = 1
    oracleText = "{T}: Destroy target creature if it shares a color with the most common color " +
        "among all permanents or a color tied for most common. A creature destroyed this way " +
        "can't be regenerated."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = ConditionalEffect(
            condition = TargetSharesMostCommonColor(),
            effect = CantBeRegeneratedEffect(creature) then Effects.Destroy(creature)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "128"
        artist = "Glen Angus"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/0047302d-4e3d-4327-9bb2-ecd5b00b00e3.jpg?1562895043"
    }
}
