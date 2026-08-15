package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kin-Tree Nurturer — Tarkir: Dragonstorm #83
 * {2}{B} · Creature — Human Druid · 2/1
 *
 * Lifelink
 * When this creature enters, it endures 1. (Put a +1/+1 counter on it or
 * create a 1/1 white Spirit creature token.)
 */
val KinTreeNurturer = card("Kin-Tree Nurturer") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Druid"
    power = 2
    toughness = 1
    oracleText = "Lifelink\n" +
        "When this creature enters, it endures 1. " +
        "(Put a +1/+1 counter on it or create a 1/1 white Spirit creature token.)"

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Endure(1)
        description = "When this creature enters, it endures 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Loïc Canavaggia"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/2177ef64-28bf-4acf-b1f1-c1408f03c411.jpg?1743204295"
    }
}
