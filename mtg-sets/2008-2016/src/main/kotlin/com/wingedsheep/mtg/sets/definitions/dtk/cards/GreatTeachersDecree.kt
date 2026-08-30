package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Great Teacher's Decree
 * {3}{W}
 * Sorcery
 *
 * Creatures you control get +2/+1 until end of turn.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * An untargeted group pump, so it is [Patterns.Group]'s `modifyStatsForAll` over
 * [GroupFilter.AllCreaturesYouControl] rather than a target requirement — the printed noun is
 * "creatures you control", the whole group, chosen fresh on each resolution. That matters for the
 * second cast: rebound (engine-live via [Keyword.REBOUND] in `StackResolver`) re-resolves the card
 * from exile on your next upkeep, and the group is recomputed then.
 */
val GreatTeachersDecree = card("Great Teacher's Decree") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Creatures you control get +2/+1 until end of turn.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        effect = Patterns.Group.modifyStatsForAll(2, 1, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "21"
        artist = "Zoltan Boros"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5ce2761c-20ee-497a-8c05-947a1ac93e57.jpg?1783938616"
    }
}
