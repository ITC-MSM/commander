package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Outnumber
 * {R}
 * Instant
 * Outnumber deals damage to target creature equal to the number of creatures you control.
 *
 * [DynamicAmounts.creaturesYouControl] is counted on resolution, per the BFZ ruling.
 */
val Outnumber = card("Outnumber") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Outnumber deals damage to target creature equal to the number of creatures you control."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(DynamicAmounts.creaturesYouControl(), creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Tyler Jacobson"
        flavorText = "Everyone who could still lift a weapon had a part in retaking Sea Gate."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3373281-7319-4320-8afb-4546fb55ab4c.jpg?1783938193"

        ruling(
            "2015-08-25",
            "Count the number of creatures you control as Outnumber resolves to determine how much " +
                "damage Outnumber deals."
        )
    }
}
