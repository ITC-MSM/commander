package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Ragnar
 * {G}{W}{U}
 * Legendary Creature — Human Cleric
 * 2/2
 *
 * {G}{W}{U}, {T}: Regenerate target creature.
 */
val Ragnar = card("Ragnar") {
    manaCost = "{G}{W}{U}"
    colorIdentity = "GUW"
    typeLine = "Legendary Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "{G}{W}{U}, {T}: Regenerate target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}{W}{U}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = RegenerateEffect(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "250"
        artist = "Melissa A. Benson"
        flavorText = "\"On the field of honor, a soldier need have no fear.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cf6a3a3-4a06-4eb7-981a-b70cf05b2473.jpg?1783948034"
    }
}
