package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vedalken Blademaster
 * {2}{U}
 * Creature — Vedalken Soldier
 * 2 / 3
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 *
 * Prowess is an engine-live keyword — the reminder text is part of the printed oracle text, so it
 * stays in `oracleText`, but the behaviour comes entirely from [Keyword.PROWESS].
 */
val VedalkenBlademaster = card("Vedalken Blademaster") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Soldier"
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"
    power = 2
    toughness = 3

    keywords(Keyword.PROWESS)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Lake Hurwitz"
        flavorText = "Vedalken have six thin, agile fingers on each hand, giving them unparalleled dexterity."
        imageUri = "https://cards.scryfall.io/normal/front/5/7/572c15ab-2229-4536-b586-638ec77d9cb7.jpg?1783937212"
    }
}
