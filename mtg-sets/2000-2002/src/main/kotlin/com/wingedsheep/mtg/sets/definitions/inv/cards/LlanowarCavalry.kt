package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Llanowar Cavalry
 * {2}{G}
 * Creature — Human Soldier
 * 1/4
 * {W}: This creature gains vigilance until end of turn.
 */
val LlanowarCavalry = card("Llanowar Cavalry") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 4
    oracleText = "{W}: This creature gains vigilance until end of turn."

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Eric Peterson"
        flavorText = "For the first time, elves welcomed Benalish soldiers into the forest with something other than arrows."
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21d92191-a743-4916-bbe4-5e207e964d9b.jpg?1562901760"
    }
}
