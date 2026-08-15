package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Adamant Will
 * {1}{W}
 * Instant
 * Target creature gets +2/+2 and gains indestructible until end of turn.
 */
val AdamantWill = card("Adamant Will") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains indestructible until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Alex Konstad"
        flavorText = "The shield took a year to craft, a month to enchant, and a decade to master—all for one glorious moment."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3dfb8817-ca3c-44ba-92f2-e9d6294cd25d.jpg?1562734428"
    }
}
