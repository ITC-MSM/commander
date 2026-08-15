package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vanish from Sight
 * {3}{U}
 * Instant
 * Target nonland permanent's owner puts it on their choice of the top or bottom of their library.
 * Surveil 1.
 *
 * `Effects.PutOnTopOrBottomOfLibrary` hands the owner the top/bottom choice (CR — the permanent's
 * owner decides), then `Patterns.Library.surveil(1)` resolves for the spell's controller.
 */
val VanishFromSight = card("Vanish from Sight") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target nonland permanent's owner puts it on their choice of the top or bottom of " +
        "their library. Surveil 1. (Look at the top card of your library. You may put it into your " +
        "graveyard.)"

    spell {
        val permanent = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.Composite(
            Effects.PutOnTopOrBottomOfLibrary(permanent),
            Patterns.Library.surveil(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Billy Christian"
        flavorText = "The Wanderer flung out her hand, but too late—Kaito had vanished into the House's maw."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/5254988b-3113-42f7-b751-517ffb3b40f0.jpg?1727205055"
    }
}
