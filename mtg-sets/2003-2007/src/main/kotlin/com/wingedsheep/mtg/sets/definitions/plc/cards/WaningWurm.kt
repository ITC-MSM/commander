package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Waning Wurm
 * {3}{B}
 * Creature — Zombie Wurm
 * 7/6
 * Vanishing 2
 */
val WaningWurm = card("Waning Wurm") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Wurm"
    power = 7
    toughness = 6
    oracleText = "Vanishing 2 (This creature enters with two time counters on it. At the beginning of your upkeep, remove a time counter from it. When the last is removed, sacrifice it.)"

    keywordAbility(KeywordAbility.vanishing(2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Alan Pollack"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b20b0048-f93a-4349-b5d1-201ab0a38d1b.jpg"
    }
}
