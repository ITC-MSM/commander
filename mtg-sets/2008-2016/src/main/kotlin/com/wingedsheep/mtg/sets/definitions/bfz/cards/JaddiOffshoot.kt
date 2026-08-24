package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jaddi Offshoot
 * {G}
 * Creature — Plant
 * 0/3
 * Defender
 * Landfall — Whenever a land you control enters, you gain 1 life.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val JaddiOffshoot = card("Jaddi Offshoot") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant"
    power = 0
    toughness = 3
    oracleText = "Defender\n" +
        "Landfall — Whenever a land you control enters, you gain 1 life."

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "176"
        artist = "Daarken"
        flavorText = "On Murasa, even the trees grow trees."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/aca704d5-b6e0-4726-8856-0b3a6732bbd8.jpg?1783938188"
    }
}
