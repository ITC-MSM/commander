package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Unyaro Bees
 * {G}{G}{G}
 * Creature — Insect
 * 0 / 1
 *
 * Flying
 * {G}: This creature gets +1/+1 until end of turn.
 * {3}{G}, Sacrifice this creature: It deals 2 damage to any target.
 */
val UnyaroBees = card("Unyaro Bees") {
    manaCost = "{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 0
    toughness = 1
    oracleText = "Flying\n" +
        "{G}: This creature gets +1/+1 until end of turn.\n" +
        "{3}{G}, Sacrifice this creature: It deals 2 damage to any target."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{G}"), Costs.SacrificeSelf)
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "231"
        artist = "Tom Wänerstrand"
        flavorText = "With no jungle left to contain it, the \"plague of daggers\" spread across Dominaria."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76356bc9-2285-44a7-815e-a27ad4e07afc.jpg"
    }
}
