package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Warhorn Blast
 * {4}{W}
 * Instant
 * Creatures you control get +2/+1 until end of turn.
 * Foretell {2}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A team pump at instant speed. One [Patterns.Group.modifyStatsForAll] pass, not a per-creature
 * composite: the printed sentence names its group once, so the group is gathered once.
 */
val WarhornBlast = card("Warhorn Blast") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Creatures you control get +2/+1 until end of turn.\n" +
        "Foretell {2}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        effect = Patterns.Group.modifyStatsForAll(
            power = 2,
            toughness = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    keywordAbility(KeywordAbility.foretell("{2}{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Bryan Sola"
        flavorText = "\"Mead down! Swords up!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3fc98aff-edc0-4f78-ae4f-e08735c9e512.jpg"
    }
}
