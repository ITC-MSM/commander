package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lash of the Whip
 * {4}{B}
 * Instant
 *
 * Target creature gets -4/-4 until end of turn.
 */
val LashOfTheWhip = card("Lash of the Whip") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -4/-4 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-4, -4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Dan Murayama Scott"
        flavorText = "\"No matter who their fickle hearts worship, all mortals belong to one god in the end.\"\n—Iadorna, death priest of Erebos"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba32cf1f-375c-4cc5-9963-c4f9510e3b39.jpg"
    }
}
