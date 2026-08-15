package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Orim, Samite Healer
 * {1}{W}{W}
 * Legendary Creature — Human Cleric
 * 1/3
 * {T}: Prevent the next 3 damage that would be dealt to any target this turn.
 */
val OrimSamiteHealer = card("Orim, Samite Healer") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Cleric"
    power = 1
    toughness = 3
    oracleText = "{T}: Prevent the next 3 damage that would be dealt to any target this turn."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", AnyTarget())
        effect = Effects.PreventNextDamage(3, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "33"
        artist = "Kaja Foglio"
        flavorText = "\"The silkworm spins itself a new existence. So the healer weaves the threads of life.\"\n—Orim, Samite healer"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/7086d077-f083-4870-8b0b-2d34aca49df1.jpg"
    }
}
