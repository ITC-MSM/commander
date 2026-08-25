package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Active Volcano
 * {R}
 * Instant
 *
 * Choose one —
 * • Destroy target blue permanent.
 * • Return target Island to its owner's hand.
 */
val ActiveVolcano = card("Active Volcano") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target blue permanent.\n" +
        "• Return target Island to its owner's hand."

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target blue permanent") {
                val permanent = target(
                    "target blue permanent",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Permanent.withColor(Color.BLUE))),
                )
                effect = Effects.Destroy(permanent)
            }
            mode("Return target Island to its owner's hand") {
                val land = target(
                    "target Island",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Land.withSubtype(Subtype.ISLAND))),
                )
                effect = Effects.ReturnToHand(land)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Justin Hampton"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad402e65-6fac-4005-a2d4-592983df0c30.jpg?1783948060"
    }
}
