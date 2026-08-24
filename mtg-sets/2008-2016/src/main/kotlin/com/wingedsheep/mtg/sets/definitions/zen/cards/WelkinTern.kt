package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Welkin Tern
 * {1}{U}
 * Creature — Bird
 * 2/1
 * Flying
 * This creature can block only creatures with flying.
 *
 * Canonical printing: Zendikar, the card's earliest real-expansion printing. Reprinted in M13,
 * M15, GS1 and PZ2 as `Printing` rows.
 */
val WelkinTern = card("Welkin Tern") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 1
    oracleText =
        "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "This creature can block only creatures with flying."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CanOnlyBlockCreaturesWith(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Austin Hsu"
        flavorText = "\"The sky hedrons are covered with tern nests. It's as though the birds have given up on the land altogether.\"\n—Ilori, merfolk falconer"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/357931d0-8ba6-4857-9db9-7f42d81514a5.jpg?1783942157"
    }
}
