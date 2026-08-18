package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Exorcist
 * {W}{W}
 * Creature — Human Cleric
 * 1/1
 * {1}{W}, {T}: Destroy target black creature.
 */
val Exorcist = card("Exorcist") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "{1}{W}, {T}: Destroy target black creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        val creature = target("target black creature", Targets.CreatureWithColor(Color.BLACK))
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Drew Tucker"
        flavorText = "Though they often bore little greater charm than the demons they battled, exorcists were always welcome in Scarwood."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/184b7d52-e991-4668-9f6a-bcded97f51ac.jpg?1783947949"
    }
}
