package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless

/**
 * Armored Galleon
 * {4}{U}
 * Creature — Human Pirate
 * 5/4
 *
 * This creature can't attack unless defending player controls an Island.
 */
val ArmoredGalleon = card("Armored Galleon") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    oracleText = "This creature can't attack unless defending player controls an Island."
    power = 5
    toughness = 4

    staticAbility {
        ability = CantAttackUnless(Conditions.DefendingPlayerControlsLandType("Island"))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "33"
        artist = "Doug Chaffee"
        flavorText = "\"Information equals profits. Our merchants sell it, or our pirates use it.\"\n—Jefan, Talas ship captain"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/484b2978-2da5-41cf-85d4-128e9dae75c0.jpg"
    }
}
