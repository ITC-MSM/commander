package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Faultgrinder
 * {6}{R}
 * Creature — Elemental
 * 4/4
 * Trample
 * When this creature enters, destroy target land.
 * Evoke {4}{R}
 */
val Faultgrinder = card("Faultgrinder") {
    manaCost = "{6}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 4
    oracleText = "Trample\nWhen this creature enters, destroy target land.\n" +
        "Evoke {4}{R} (You may cast this spell for its evoke cost. If you do, it's sacrificed " +
        "when it enters.)"

    keywords(Keyword.TRAMPLE)

    evoke = "{4}{R}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val land = target("target land", Targets.Land)
        effect = Effects.Destroy(land)
        description = "destroy target land."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Anthony S. Waters"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be88b32a-986c-4317-b4a3-119f67e13b83.jpg?1783942877"
    }
}
