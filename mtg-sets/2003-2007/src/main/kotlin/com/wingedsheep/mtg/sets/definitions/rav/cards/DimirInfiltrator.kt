package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity

val DimirInfiltrator = card("Dimir Infiltrator") {
    manaCost = "{U}{B}"
    typeLine = "Creature — Spirit"
    oracleText = "This creature can't be blocked.\nTransmute {1}{U}{B} ({1}{U}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "BU"
    power = 1
    toughness = 3

    staticAbility { ability = CantBeBlocked() }
    transmute("{1}{U}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "203"
        artist = "Jim Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3db9204c-dde8-4241-aac2-1f090566f604.jpg?1783943621"
    }
}
