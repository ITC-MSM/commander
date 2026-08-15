package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rainbow Crow
 * {3}{U}
 * Creature — Bird
 * 2/2
 * Flying
 * {1}: This creature becomes the color of your choice until end of turn.
 */
val RainbowCrow = card("Rainbow Crow") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 2
    oracleText = "Flying\n{1}: This creature becomes the color of your choice until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.ChangeColorToChosen(EffectTarget.Self)
        description = "{1}: This creature becomes the color of your choice until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "69"
        artist = "Edward P. Beard, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e622ad2-473f-489e-b4cf-bbdcc44d0cde.jpg?1562920499"
    }
}
