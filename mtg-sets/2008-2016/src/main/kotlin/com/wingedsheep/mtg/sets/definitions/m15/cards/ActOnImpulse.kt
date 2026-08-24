package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Act on Impulse
 * {2}{R}
 * Sorcery
 * Exile the top three cards of your library. Until end of turn, you may play those cards.
 *
 * Plain impulse draw at count 3 ([Patterns.Exile.impulse]) — gather the top three, move them to
 * exile, grant "you may play them" until end of turn. The cards still cost their mana.
 */
val ActOnImpulse = card("Act on Impulse") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Exile the top three cards of your library. Until end of turn, you may play those cards. (If you cast a spell this way, you still pay its costs. You can play a land this way only if you have an available land play remaining.)"

    spell {
        effect = Patterns.Exile.impulse(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Brad Rigney"
        flavorText = "\"You don't want to know what happens after I put on the goggles.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfe513d1-2509-4051-ba85-49a19479fa5c.jpg?1783939177"
    }
}
