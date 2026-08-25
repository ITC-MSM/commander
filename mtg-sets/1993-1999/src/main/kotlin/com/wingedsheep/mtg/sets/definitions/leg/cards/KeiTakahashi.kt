package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kei Takahashi
 * {2}{G}{W}
 * Legendary Creature — Human Cleric
 * 2/2
 *
 * {T}: Prevent the next 2 damage that would be dealt to target creature this turn.
 */
val KeiTakahashi = card("Kei Takahashi") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "{T}: Prevent the next 2 damage that would be dealt to target creature this turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.PreventNextDamage(2, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "238"
        artist = "Scott Kirschner"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a4a524a-fdc7-432d-994b-953808528349.jpg?1783948036"
    }
}
