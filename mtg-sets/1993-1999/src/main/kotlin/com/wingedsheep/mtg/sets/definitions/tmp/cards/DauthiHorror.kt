package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Dauthi Horror
 * {1}{B}
 * Creature — Dauthi Horror
 * 2/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * This creature can't be blocked by white creatures.
 *
 * Two independent evasion rules stacked: [Keyword.SHADOW] is read directly by the block-evasion
 * rules, and the white clause is a separate [CantBeBlockedBy] over a colored creature filter — the
 * two narrow each other rather than composing into one filter.
 */
val DauthiHorror = card("Dauthi Horror") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Dauthi Horror"
    power = 2
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "This creature can't be blocked by white creatures."

    keywords(Keyword.SHADOW)

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withColor(Color.WHITE))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Jeff Laubenstein"
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5a8bb3a-3a84-442f-8e31-8af2f04408ab.jpg?1783946642"
    }
}
