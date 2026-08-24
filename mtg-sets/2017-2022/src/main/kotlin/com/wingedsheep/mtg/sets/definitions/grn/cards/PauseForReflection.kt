package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pause for Reflection
 * {2}{G}
 * Instant
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Prevent all combat damage that would be dealt this turn.
 */
val PauseForReflection = card("Pause for Reflection") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Prevent all combat damage that would be dealt this turn."

    keywords(Keyword.CONVOKE)
    spell {
        effect = Effects.PreventAllCombatDamage()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cfcf84c-a30c-4c4f-9f8c-ee807661e499.jpg?1783934148"
    }
}
