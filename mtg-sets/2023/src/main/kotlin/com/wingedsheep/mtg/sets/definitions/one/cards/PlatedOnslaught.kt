package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Plated Onslaught
 * {3}{W}{W}
 * Instant
 *
 * Affinity for artifacts (This spell costs {1} less to cast for each artifact you control.)
 * Creatures you control get +2/+1 until end of turn.
 *
 * Affinity is [KeywordAbility.Affinity] — its own cost-calculator branch, not a `ModifySpellCost`.
 * The pump is a mass, non-targeted group effect over every creature you control.
 */
val PlatedOnslaught = card("Plated Onslaught") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Affinity for artifacts (This spell costs {1} less to cast for each artifact you control.)\n" +
        "Creatures you control get +2/+1 until end of turn."

    keywordAbility(KeywordAbility.Affinity(CardType.ARTIFACT))

    spell {
        effect = Patterns.Group.modifyStatsForAll(
            power = 2,
            toughness = 1,
            filter = GroupFilter.AllCreaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "David Auden Nash"
        imageUri = "https://cards.scryfall.io/normal/front/4/7/472a635a-a581-4c00-89d6-464f30887944.jpg?1783918075"
    }
}
