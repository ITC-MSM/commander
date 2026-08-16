package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Inspired Charge
 * {2}{W}{W}
 * Instant
 * Creatures you control get +2/+1 until end of turn.
 *
 * Canonical printing: Magic 2011, the card's earliest real-expansion printing. Reprinted in MOM
 * (and elsewhere) as a `Printing` row.
 */
val InspiredCharge = card("Inspired Charge") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Creatures you control get +2/+1 until end of turn."

    spell {
        effect = Patterns.Group.modifyStatsForAll(
            power = 2,
            toughness = 1,
            filter = GroupFilter.AllCreaturesYouControl
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Wayne Reynolds"
        flavorText = "\"Impossible! How could they overwhelm us? We had barricades, war elephants, " +
            ". . . and they were barely a tenth of our number!\"\n—General Avitora"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98151986-90de-4f76-9abd-507039e7c9ad.jpg?1783941834"
        ruling(
            "2016-09-20",
            "The set of creatures affected by Inspired Charge is determined as the spell resolves. " +
                "Creatures you begin to control later in the turn and noncreature permanents that " +
                "become creatures later in the turn won't get +2/+1."
        )
    }
}
