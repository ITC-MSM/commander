package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.champion
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanBlockAnyNumber

/**
 * Thoughtweft Trio
 * {2}{W}{W}
 * Creature — Kithkin Soldier
 * 5/5
 *
 * First strike, vigilance
 * Champion a Kithkin
 * This creature can block any number of creatures.
 */
val ThoughtweftTrio = card("Thoughtweft Trio") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 5
    toughness = 5
    oracleText = "First strike, vigilance\n" +
        "Champion a Kithkin (When this enters, sacrifice it unless you exile another Kithkin you " +
        "control. When this leaves the battlefield, that card returns to the battlefield.)\n" +
        "This creature can block any number of creatures."

    keywords(Keyword.FIRST_STRIKE, Keyword.VIGILANCE)
    champion(Subtype.KITHKIN)

    staticAbility {
        ability = CanBlockAnyNumber()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "44"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e48d34d-063c-4703-870b-4a22d5774c89.jpg?1783942908"
    }
}
