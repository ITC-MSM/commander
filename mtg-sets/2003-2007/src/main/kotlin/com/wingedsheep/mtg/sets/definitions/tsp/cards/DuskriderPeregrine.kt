package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Duskrider Peregrine
 * {5}{W}
 * Creature — Bird
 * 3 / 3
 * Flying, protection from black
 * Suspend 3—{1}{W} (Rather than cast this card from your hand, you may pay {1}{W} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * Protection is parameterized rather than a bare enum — [KeywordAbility.protectionFrom] over a
 * [com.wingedsheep.sdk.scripting.ProtectionScope.Color], which is why it carries no `Keyword`
 * entry of its own. Suspend is the usual `suspend(cost, timeCounters)` one-liner.
 */
val DuskriderPeregrine = card("Duskrider Peregrine") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    power = 3
    toughness = 3
    oracleText = "Flying, protection from black\n" +
        "Suspend 3—{1}{W} (Rather than cast this card from your hand, you may pay {1}{W} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.protectionFrom(Color.BLACK))
    keywordAbility(KeywordAbility.suspend("{1}{W}", 3))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Una Fricker"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfad8c3e-e459-477c-b602-34df2dda1efe.jpg"
    }
}
