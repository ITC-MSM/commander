package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pixie Queen
 * {2}{G}{G}
 * Creature — Faerie
 * 1/1
 *
 * Flying
 * {G}{G}{G}, {T}: Target creature gains flying until end of turn.
 */
val PixieQueen = card("Pixie Queen") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Faerie"
    power = 1
    toughness = 1
    oracleText = "Flying\n{G}{G}{G}, {T}: Target creature gains flying until end of turn."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}{G}{G}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "196"
        artist = "Quinton Hoover"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9527c2a-23bb-4d33-9e72-6e0ab3de0e6b.jpg?1783948046"
    }
}
