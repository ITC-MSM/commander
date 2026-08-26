package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Desert Cerodon
 * {5}{R}
 * Creature — Beast
 * 6/4
 * Cycling {R} ({R}, Discard this card: Draw a card.)
 */
val DesertCerodon = card("Desert Cerodon") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    oracleText = "Cycling {R} ({R}, Discard this card: Draw a card.)"
    power = 6
    toughness = 4

    keywordAbility(KeywordAbility.cycling("{R}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Igor Kieryluk"
        flavorText = "The endless expanse of desert surrounding Naktamun sometimes yields threats that the gods themselves must answer."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/2047c2e5-8b3b-4c6b-91cf-3484f21e52f0.jpg?1783936491"
    }
}
