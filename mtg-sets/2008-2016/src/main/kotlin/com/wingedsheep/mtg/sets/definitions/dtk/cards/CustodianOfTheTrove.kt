package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Custodian of the Trove
 * {3}
 * Artifact Creature — Golem
 * 2 / 5
 *
 * Defender
 * This creature enters tapped.
 *
 * "Enters tapped" is a self-replacement (CR 614.1c), not an as-enters effect, so it is
 * `replacementEffect(EntersTapped())` — the same spelling every tapped-land in the corpus uses.
 * Defender is the plain keyword beside it.
 */
val CustodianOfTheTrove = card("Custodian of the Trove") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 2
    toughness = 5
    oracleText = "Defender\n" +
        "This creature enters tapped."

    keywords(Keyword.DEFENDER)

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "236"
        artist = "Raoul Vitale"
        flavorText = "Silumgar delights in repurposing the treasures of other clans to serve his own ravenous greed."
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb14f061-9506-40fb-b10d-4bada38ca0f7.jpg?1783938569"
    }
}
