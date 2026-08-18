package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chromatic Sphere
 * {1}
 * Artifact
 *
 * {1}, {T}, Sacrifice this artifact: Add one mana of any color. Draw a card.
 *
 * Not a mana ability. CR 605.1a (August 7, 2026) added "and its cost and effect don't move any card
 * to or from a library" to the criteria, and the draw is exactly that — so this is an ordinary
 * activated ability: it uses the stack, can be countered or responded to, and can't be activated
 * while paying a cost. The 2008 ruling below describes the pre-update classification and is kept
 * because it is what Scryfall still carries; it no longer describes how the card plays.
 */
val ChromaticSphere = card("Chromatic Sphere") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}, Sacrifice this artifact: Add one mana of any color. Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddAnyColorMana(1),
            Effects.DrawCards(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "299"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/9/2/920cd17f-9274-443e-906f-c9904f0658d5.jpg?1562924494"
        ruling(
            "2008-08-01",
            "This is a mana ability, which means it can be activated as part of the process of casting a spell or activating another ability. If that happens you get the mana right away, but you don't get to look at the drawn card until you have finished casting that spell or activating that ability.",
        )
    }
}
