package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.UntapLimitPerStep

/**
 * Damping Field
 * {2}{W}
 * Enchantment
 * Players can't untap more than one artifact during their untap steps.
 *
 * A global untap-count cap modeled by [UntapLimitPerStep] (filter = artifacts, max = 1). It applies
 * to every player's untap step regardless of who controls Damping Field: when a player has more than
 * one tapped artifact that would untap, the untap step makes them keep the excess tapped, choosing
 * which single artifact untaps. (Originally an Artifact in 1994; the modern Oracle printing is an
 * Enchantment.)
 */
val DampingField = card("Damping Field") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Players can't untap more than one artifact during their untap steps."

    staticAbility {
        ability = UntapLimitPerStep(GameObjectFilter.Artifact, 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "5"
        artist = "Justin Hampton"
        flavorText = "Eventually, mages learned to harness the power of natural damping fields and use it for their own ends."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12ab9836-bc90-4d92-a86d-b8e1b7671aa7.jpg?1562898915"
    }
}
