package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Inkling Summoning — Strixhaven: School of Mages #195 (canonical printing)
 * {1}{W/B}{W/B} · Sorcery — Lesson
 *
 * Create a 2/1 white and black Inkling creature token with flying.
 *
 * A single [Effects.CreateToken]: a 2/1 white-and-black Inkling with flying. Token art resolves
 * through STX's synced token printings, so none is declared here. Lesson is only a subtype.
 */
val InklingSummoning = card("Inkling Summoning") {
    manaCost = "{1}{W/B}{W/B}"
    colorIdentity = "BW"
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Create a 2/1 white and black Inkling creature token with flying."

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 1,
            colors = setOf(Color.WHITE, Color.BLACK),
            creatureTypes = setOf("Inkling"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Scott Murphy"
        flavorText = "\"Think of an insult so vicious, so vulgar, that you hesitate to speak it aloud. The strongest, most aggressive inklings are born from such scorn.\"\n—Embrose, Silverquill dean"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04a8a5b8-9743-4d1a-89e9-61bdf180b2e0.jpg?1783927310"
    }
}
