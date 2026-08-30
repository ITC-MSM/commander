package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vandalize
 * {4}{R}
 * Sorcery
 *
 * Choose one or both —
 * • Destroy target artifact.
 * • Destroy target land.
 *
 * "Choose one or both" is a *count* over the two printed modes, not a third "do both" mode:
 * `chooseCount = 2` with `minChooseCount = 1` (CR 700.2d), which the SDK renders back as
 * "one or both". See Scour for Scrap for the same correction — an authored "do both" mode reports
 * one chosen mode where the spell actually chose two, which is the Winterflame regression. Each
 * mode declares its own target, so only the chosen modes' targets are chosen on announcement.
 */
val Vandalize = card("Vandalize") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Destroy target artifact.\n" +
        "• Destroy target land."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Destroy target artifact") {
                val artifact = target("target", Targets.Artifact)
                effect = Effects.Destroy(artifact)
            }
            mode("Destroy target land") {
                val land = target("target", Targets.Land)
                effect = Effects.Destroy(land)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Ryan Barger"
        flavorText = "\"As we have learned from Kolaghan, to ruin is to rule.\"\n—Shensu, Kolaghan rider"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48b04f7a-4fd6-47d2-b378-99c7fb0c1809.jpg?1783938584"
    }
}
