package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Reprieve
 * {1}{W}
 * Instant
 *
 * Return target spell to its owner's hand.
 * Draw a card.
 */
val Reprieve = card("Reprieve") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Return target spell to its owner's hand.\nDraw a card."

    spell {
        target("spell", Targets.Spell)
        effect = Effects.ReturnSpellToOwnersHand() then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "26"
        artist = "Justyna Dura"
        flavorText = "As Faramir and Éowyn stood so, their hands met and clasped, though they did not know it."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bd3fa8a-6c50-4f7f-9ae3-0810eec5e3db.jpg?1686967885"
    }
}
