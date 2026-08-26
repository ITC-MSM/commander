package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Read the Bones
 * {2}{B}
 * Sorcery
 *
 * Scry 2, then draw two cards. You lose 2 life. (To scry 2, look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 */
val ReadTheBones = card("Read the Bones") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Scry 2, then draw two cards. You lose 2 life. (To scry 2, look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    spell {
        effect = Effects.Composite(
            Effects.Scry(2),
            Effects.DrawCards(2),
            Effects.LoseLife(2, EffectTarget.Controller)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Lars Grant-West"
        flavorText = "The dead know lessons the living haven't learned."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbbdbf1a-2d15-4291-aa19-614f854d8cb3.jpg"
    }
}
