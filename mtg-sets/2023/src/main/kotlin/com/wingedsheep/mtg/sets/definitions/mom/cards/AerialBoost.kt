package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aerial Boost
 * {1}{W}
 * Instant
 * Convoke
 * Target creature gets +2/+2 and gains flying until end of turn.
 *
 * Convoke (CR 702.51) is a pure cost-payment keyword, so the spell body is the ordinary
 * pump-and-grant pair over one named target.
 */
val AerialBoost = card("Aerial Boost") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Target creature gets +2/+2 and gains flying until end of turn."

    keywords(Keyword.CONVOKE)

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature) then
            Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Artur Nakhodkin"
        flavorText = "\"Ha! You missed!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7017afb-4c7c-4c8d-9c9d-3f056a55561e.jpg?1783917072"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
        ruling(
            "2024-01-12",
            "Tapping an untapped creature that's attacking or blocking to convoke a spell won't " +
                "cause that creature to stop attacking or blocking."
        )
    }
}
