package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity

val MuddleTheMixture = card("Muddle the Mixture") {
    manaCost = "{U}{U}"
    typeLine = "Instant"
    oracleText = "Counter target instant or sorcery spell.\nTransmute {1}{U}{U} ({1}{U}{U}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "U"

    spell {
        target("target instant or sorcery spell", Targets.InstantOrSorcerySpell)
        effect = Effects.CounterSpell()
    }
    transmute("{1}{U}{U}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Luca Zontini"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4cc785b0-0a77-4b02-b0b4-2bda2fc621cc.jpg?1783943681"
    }
}
