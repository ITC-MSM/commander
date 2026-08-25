package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Durkwood Baloth
 * {4}{G}{G}
 * Creature — Beast
 * 5 / 5
 * Suspend 5—{G} (Rather than cast this card from your hand, you may pay {G} and exile it with five time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * A vanilla body plus suspend, written once as the parameterized [KeywordAbility.Suspend]
 * (`suspend(cost, timeCounters)`); `Keyword.SUSPEND` is derived from it by `CardBuilder.build()`.
 */
val DurkwoodBaloth = card("Durkwood Baloth") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Suspend 5—{G} (Rather than cast this card from your hand, you may pay {G} and exile it with five time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywordAbility(KeywordAbility.suspend("{G}", 5))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/670521c3-df02-487d-a299-49419e41889f.jpg"
    }
}
