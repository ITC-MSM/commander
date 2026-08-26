package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Assault Griffin
 * {3}{W}
 * Creature — Griffin
 * 3/2
 *
 * Flying
 */
val AssaultGriffin = card("Assault Griffin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 3
    toughness = 2
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Jesper Ejsing"
        flavorText = "\"Fine soldiers guarded the northern front. They waited for two-legged foes and left the skies unheeded.\"\n" +
            "—General Avitora"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f72ced22-1f2c-4fa6-a938-8ebe2c15cc8d.jpg?1783941837"
    }
}
