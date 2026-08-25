package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Magmatic Sinkhole
 * {5}{R}
 * Instant
 * Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)
 * Magmatic Sinkhole deals 5 damage to target creature or planeswalker.
 *
 * Murderous Cut's shape in red: [Keyword.DELVE] is read by the cast-time cost payment, so the bare
 * keyword is the whole of the first line, and the body is one [Effects.DealDamage] over
 * [Targets.CreatureOrPlaneswalker].
 */
val MagmaticSinkhole = card("Magmatic Sinkhole") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)\n" +
        "Magmatic Sinkhole deals 5 damage to target creature or planeswalker."

    keywords(Keyword.DELVE)

    spell {
        val t = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(5, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Mark Behm"
        flavorText = "Opening like the maw of a hellion, the earth swallowed the traveler whole."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94db1674-d72b-4c1f-b436-490e5363bee7.jpg?1783933110"
    }
}
