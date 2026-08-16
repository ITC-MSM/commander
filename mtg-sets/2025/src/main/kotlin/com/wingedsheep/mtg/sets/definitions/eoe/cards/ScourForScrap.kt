package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Scour for Scrap
 * {3}{U}
 * Instant
 * Choose one or both —
 * • Search your library for an artifact card, reveal it, put it into your hand, then shuffle.
 * • Return target artifact card from your graveyard to your hand.
 */
val ScourForScrap = card("Scour for Scrap") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Search your library for an artifact card, reveal it, put it into your hand, then shuffle.\n" +
        "• Return target artifact card from your graveyard to your hand."

    spell {
        // "Choose one or both" is the *count*, not a third mode — `chooseCount = 2` with
        // `minChooseCount = 1` (CR 700.2). See Winterflame for the same correction: an extra
        // "do both" mode reports one chosen mode where the spell chose two.
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Search your library for an artifact card, reveal it, put it into your hand, then shuffle") {
                effect = Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.Artifact,
                    count = 1,
                    destination = SearchDestination.HAND,
                    reveal = true,
                    shuffleAfter = true
                )
            }
            mode("Return target artifact card from your graveyard to your hand") {
                val artifact = target(
                    "artifact card in your graveyard",
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Artifact.ownedByYou(), zone = Zone.GRAVEYARD)
                    ),
                )
                effect = Effects.ReturnToHand(artifact)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Filip Burburan"
        flavorText = "The Illvoi hunt for stellar fragments that predate even the Fomori."
        imageUri = "https://cards.scryfall.io/normal/front/5/1/517d1b00-7ec4-489a-ac52-657da24a6379.jpg?1752946845"
    }
}
