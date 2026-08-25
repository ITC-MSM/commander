package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Facet Reader
 * {1}{U}
 * Creature — Human Wizard
 * 1/2
 * {1}, {T}: Draw a card, then discard a card.
 *
 * The looter shape, [Patterns.Hand.loot]: a draw followed by the Gather → Select → Move discard
 * pipeline. Gathering the hand *after* the draw resolves is what "then" means here — the
 * just-drawn card is a legal choice for the discard.
 */
val FacetReader = card("Facet Reader") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 2
    oracleText = "{1}, {T}: Draw a card, then discard a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Patterns.Hand.loot()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Matt Stewart"
        flavorText = "\"Every flaw in the crystal represents a moment where our strategy might go wrong.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1199596-ae77-4192-ae70-3e2ebd009b64.jpg"
    }
}
