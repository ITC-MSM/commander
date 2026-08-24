package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pack's Favor
 * {2}{G}
 * Instant
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Target creature gets +3/+3 until end of turn.
 */
val PacksFavor = card("Pack's Favor") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Target creature gets +3/+3 until end of turn."

    keywords(Keyword.CONVOKE)
    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(3, 3, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Lius Lasahido"
        flavorText = "Selesnya grows its ranks in more ways than one."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f88c3aa-63e1-4617-bf1f-48f44988e7d6.jpg?1783934147"
    }
}
