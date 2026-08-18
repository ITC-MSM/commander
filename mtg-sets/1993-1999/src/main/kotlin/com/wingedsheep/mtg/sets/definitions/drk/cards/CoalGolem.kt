package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Coal Golem
 * {5}
 * Artifact Creature — Golem
 * 3/3
 * {3}, Sacrifice this creature: Add {R}{R}{R}.
 */
val CoalGolem = card("Coal Golem") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3
    oracleText = "{3}, Sacrifice this creature: Add {R}{R}{R}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.SacrificeSelf)
        effect = Effects.AddMana(Color.RED, 3)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Christopher Rush"
        flavorText = "\"Three such creatures stood burning at the crest of the hill. Only seconds later, the Fireball struck our front line.\" —Lydia, Countess Brellis"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1ad7692d-5a51-493f-a322-7b615446ea8e.jpg?1783947927"
    }
}
