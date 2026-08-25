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
 * Flash Flood
 * {U}
 * Instant
 *
 * Choose one —
 * • Destroy target red permanent.
 * • Return target Mountain to its owner's hand.
 */
val FlashFlood = card("Flash Flood") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target red permanent.\n" +
        "• Return target Mountain to its owner's hand."

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target red permanent") {
                val permanent = target(
                    "target red permanent",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Permanent.withColor(Color.RED))),
                )
                effect = Effects.Destroy(permanent)
            }
            mode("Return target Mountain to its owner's hand") {
                val land = target(
                    "target Mountain",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Land.withSubtype(Subtype.MOUNTAIN))),
                )
                effect = Effects.ReturnToHand(land)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Tom Wänerstrand"
        flavorText = "Many people say that no power can bring the mountains low. Many people are fools."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5ae88c06-f28c-4fbc-a28c-5eb203a04722.jpg?1783948076"
    }
}
