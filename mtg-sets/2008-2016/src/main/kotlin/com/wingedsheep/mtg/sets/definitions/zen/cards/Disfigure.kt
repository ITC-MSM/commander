package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Disfigure
 * {B}
 * Instant
 * Target creature gets -2/-2 until end of turn.
 */
val Disfigure = card("Disfigure") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -2/-2 until end of turn."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(-2, -2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Justin Sweet"
        flavorText = "\"Brave scar or unfortunate tale? It all depends on your pain threshold.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3842ad2-a449-4963-8c96-276554125757.jpg?1783942155"
    }
}
