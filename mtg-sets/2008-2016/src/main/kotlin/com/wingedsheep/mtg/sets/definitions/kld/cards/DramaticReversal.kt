package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Dramatic Reversal
 * {1}{U}
 * Instant
 * Untap all nonland permanents you control.
 *
 * "All ... you control" is a group operation, not a target: [Patterns.Group].`untapGroup` over a
 * [GroupFilter] of `NonlandPermanent.youControl()`.
 */
val DramaticReversal = card("Dramatic Reversal") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Untap all nonland permanents you control."

    spell {
        effect = Patterns.Group.untapGroup(
            GroupFilter(GameObjectFilter.NonlandPermanent.youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Eric Deschamps"
        flavorText = "\"Nobody asked you to butt in, Jace. But thanks.\"\n—Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcb59045-2743-48ae-8063-727e551b1c41.jpg?1783937222"
    }
}
