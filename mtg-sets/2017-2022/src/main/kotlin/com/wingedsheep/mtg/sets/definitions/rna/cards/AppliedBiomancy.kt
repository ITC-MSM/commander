package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Applied Biomancy — Ravnica Allegiance #153
 * {G}{U} · Instant
 *
 * "Choose one or both" is the modal *count*, not a third mode: `chooseCount = 2` with
 * `minChooseCount = 1` (CR 700.2). Each mode carries its own target, so picking both asks for
 * two creatures — which may be the same creature twice, since the requirements are independent.
 */
val AppliedBiomancy = card("Applied Biomancy") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Target creature gets +1/+1 until end of turn.\n" +
        "• Return target creature to its owner's hand."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Target creature gets +1/+1 until end of turn") {
                val creature = target("target", Targets.Creature)
                effect = Effects.ModifyStats(1, 1, creature)
            }
            mode("Return target creature to its owner's hand") {
                val creature = target("target", Targets.Creature)
                effect = Effects.ReturnToHand(creature)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Sidharth Chaturvedi"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f91ed618-7b0b-4a70-95ad-d9ed46e28692.jpg"
    }
}
