package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gift of Strength — Hour of Devastation #117
 * {1}{G} · Instant
 *
 * The canonical lives here, in Hour of Devastation — its earliest real printing — and RNA and
 * THB carry Printing rows. One clause about one creature, so the pump and the reach grant are
 * one composite.
 */
val GiftOfStrength = card("Gift of Strength") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+3 and gains reach until end of turn."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(listOf(
            Effects.ModifyStats(3, 3, creature),
            Effects.GrantKeyword(Keyword.REACH, creature)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "117"
        artist = "Kieran Yanner"
        flavorText = "\"What greater testament can there be to Rhonas's lessons?\""
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b779620-6bac-445e-a6fe-6d770c0a9c70.jpg"
    }
}
