package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Branchblight Stalker
 * {1}{G}
 * Creature — Phyrexian Elf Scout
 * 3/1
 *
 * Toxic 2 (Players dealt combat damage by this creature also get two poison counters.)
 */
val BranchblightStalker = card("Branchblight Stalker") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Elf Scout"
    power = 3
    toughness = 1
    oracleText = "Toxic 2 (Players dealt combat damage by this creature also get two poison counters.)"

    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 2))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Krharts"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5290a4c-1f98-4e97-a407-f951e386b8b0.jpg?1783918019"
    }
}
