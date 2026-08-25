package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Plunder
 * {4}{R}
 * Sorcery
 * Destroy target artifact or land.
 * Suspend 4—{1}{R} (Rather than cast this card from your hand, you may pay {1}{R} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)
 *
 * Stock removal: [Targets.ArtifactOrLand] plus [Effects.Destroy], which lowers to a
 * graveyard move flagged `byDestruction` so indestructible and regeneration are honoured.
 */
val Plunder = card("Plunder") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or land.\n" +
        "Suspend 4—{1}{R} (Rather than cast this card from your hand, you may pay {1}{R} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)"

    spell {
        val t = target("target", Targets.ArtifactOrLand)
        effect = Effects.Destroy(t)
    }

    keywordAbility(KeywordAbility.suspend("{1}{R}", 4))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Thomas M. Baxa"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79cff220-b1a6-4da6-b765-25a583b33de2.jpg"
    }
}
