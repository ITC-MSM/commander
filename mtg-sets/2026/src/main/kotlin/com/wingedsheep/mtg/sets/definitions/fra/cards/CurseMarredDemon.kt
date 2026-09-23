package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val CurseMarredDemon = card("Curse-Marred Demon") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Demon"
    power = 4
    toughness = 4
    oracleText = "Flying, trample\n" +
        "When this creature enters, search your library for a card, put it into your hand, shuffle, " +
        "then discard a card at random."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.searchLibrary(),
            Patterns.Hand.discardRandom(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "79"
        artist = "Kev Walker"
        flavorText = "He had gambled and accepted the pact, ignorant of what he stood to lose."
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f827e50-0a08-4bc8-98b1-b26c9af15ef2.jpg?1789470818"
        inBooster = false
    }
}
