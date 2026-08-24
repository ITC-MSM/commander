package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cavern Thoctar
 * {5}{G}
 * Creature — Beast
 * 5 / 5
 * {1}{R}: This creature gets +1/+0 until end of turn.
 *
 * Firebreathing with an off-colour activation cost. There is no target — "this creature" is the
 * source — so the pump is [Effects.ModifyStats] on [EffectTarget.Self], whose default
 * `Duration.EndOfTurn` is the printed "until end of turn"; the red pip in the cost is what carries
 * the card's `GR` colour identity.
 */
val CavernThoctar = card("Cavern Thoctar") {
    manaCost = "{5}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "{1}{R}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Jean-Sébastien Rossbach"
        flavorText = "Natives of Naya know better than to loiter near the mouth of a cave. Two glowing red eyes and the stench of foul breath are all the warning you're likely to get."
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34748acb-7045-42b6-a93f-a3f11a1bc839.jpg"
    }
}
