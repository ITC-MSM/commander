package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Corpulent Corpse
 * {5}{B}
 * Creature — Zombie
 * 3 / 3
 * Fear (This creature can't be blocked except by artifact creatures and/or black creatures.)
 * Suspend 5—{B} (Rather than cast this card from your hand, you may pay {B} and exile it with five time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * Keywords only. `Keyword.SUSPEND` is display-only — the engine's `SuspendEnumerator` reads the
 * parameterized [KeywordAbility.Suspend], so suspend is written once as `suspend(cost, counters)`
 * exactly as on `tsp/cards/SearchForTomorrow.kt`; the enum is derived from it.
 */
val CorpulentCorpse = card("Corpulent Corpse") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 3
    toughness = 3
    oracleText = "Fear (This creature can't be blocked except by artifact creatures and/or black creatures.)\n" +
        "Suspend 5—{B} (Rather than cast this card from your hand, you may pay {B} and exile it with five time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywords(Keyword.FEAR)

    keywordAbility(KeywordAbility.suspend("{B}", 5))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Doug Chaffee"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a58b842a-a4c0-475d-a8b3-62d4e5bb2eaf.jpg"
    }
}
