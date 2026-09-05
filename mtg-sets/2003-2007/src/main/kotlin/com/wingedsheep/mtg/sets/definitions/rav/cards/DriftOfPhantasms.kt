package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity

val DriftOfPhantasms = card("Drift of Phantasms") {
    manaCost = "{2}{U}"
    typeLine = "Creature — Spirit"
    oracleText = "Defender (This creature can't attack.)\nFlying\nTransmute {1}{U}{U} ({1}{U}{U}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "U"
    power = 0
    toughness = 5

    keywords(Keyword.DEFENDER, Keyword.FLYING)
    transmute("{1}{U}{U}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Michael Phillippi"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1096ce5-f776-4028-b231-e6eaee35014b.jpg?1783943687"
    }
}
