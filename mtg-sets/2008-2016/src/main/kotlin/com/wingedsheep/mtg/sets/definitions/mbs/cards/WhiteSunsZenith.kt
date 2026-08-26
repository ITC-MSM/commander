package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * White Sun's Zenith — Mirrodin Besieged #19 (canonical / earliest real printing, 2011)
 * {X}{W}{W}{W} · Instant
 *
 * Create X 2/2 white Cat creature tokens. Shuffle White Sun's Zenith into its owner's library.
 *
 * The dynamic-count `Effects.CreateToken` overload, with X read off this spell's cast. Mirrodin
 * Besieged printed no Cat token of its own, so the art is the one Scryfall links from this
 * printing (`all_parts`).
 */
val WhiteSunsZenith = card("White Sun's Zenith") {
    manaCost = "{X}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Create X 2/2 white Cat creature tokens. Shuffle White Sun's Zenith into its " +
        "owner's library."

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.XValue,
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Cat"),
            imageUri = "https://cards.scryfall.io/normal/front/7/d/7d400b41-813d-4a63-848f-5eb4db4bf3bb.jpg?1783903575",
        )
        selfShuffleIntoLibrary()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "19"
        artist = "Mike Bierek"
        flavorText = "After the Battle of Liet Field, the white sun crested above Taj-Nar, " +
            "bringing hope to all who survived the carnage."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a879940e-6632-47c5-a30e-d29a82d16e9d.jpg?1783941389"
        ruling(
            "2011-06-01",
            "If this spell doesn't resolve, none of its effects occur. In particular, it will go " +
                "to the graveyard rather than to its owner's library."
        )
    }
}
