package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Coordinated Charge
 * {4}{W}
 * Instant
 *
 * Creatures you control get +2/+1 until end of turn.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * Untargeted: the pump snapshots the battlefield as the spell resolves (CR 611.2c), so
 * creatures that enter afterwards get nothing.
 */
val CoordinatedCharge = card("Coordinated Charge") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Creatures you control get +2/+1 until end of turn.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        effect = Patterns.Group.modifyStatsForAll(2, 1, Filters.Group.creaturesYouControl)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Zoltan Boros"
        flavorText = "\"Out here we're nothing but our training.\"\n—Ethuk, Coppercoat mage"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/129c404b-2c1e-4f0b-bb4e-7e8e627a69a8.jpg"
    }
}
