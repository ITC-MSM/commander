package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Spectral Snatcher
 * {4}{B}{B}
 * Creature — Spirit
 * 6/5
 *
 * Ward—Discard a card. (Whenever this creature becomes the target of a spell or ability an
 * opponent controls, counter it unless that player discards a card.)
 * Swampcycling {2} ({2}, Discard this card: Search your library for a Swamp card, reveal it,
 * put it into your hand, then shuffle.)
 */
val SpectralSnatcher = card("Spectral Snatcher") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 6
    toughness = 5
    oracleText = "Ward—Discard a card. (Whenever this creature becomes the target of a spell or " +
        "ability an opponent controls, counter it unless that player discards a card.)\n" +
        "Swampcycling {2} ({2}, Discard this card: Search your library for a Swamp card, reveal it, " +
        "put it into your hand, then shuffle.)"

    keywordAbility(KeywordAbility.wardDiscard())
    keywordAbility(KeywordAbility.typecycling("Swamp", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Domenico Cava"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68f90f57-94a0-4ee1-9383-093e8fca52ea.jpg?1726286280"
    }
}
