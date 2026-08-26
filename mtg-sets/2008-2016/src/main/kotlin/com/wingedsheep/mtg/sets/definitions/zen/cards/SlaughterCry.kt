package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Slaughter Cry
 * {2}{R}
 * Instant
 * Target creature gets +3/+0 and gains first strike until end of turn. (It deals combat damage before creatures without first strike.)
 */
val SlaughterCry = card("Slaughter Cry") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+0 and gains first strike until end of turn. (It deals combat damage before creatures without first strike.)"

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 0, creature),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Matt Cavotta"
        flavorText = "\"Since when did 'AIIIEEEE!' become a negotiation tactic?\"\n—Nikou, Joraga bard"
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c93b0eda-693e-4a17-be1d-1df162702146.jpg"
    }
}
