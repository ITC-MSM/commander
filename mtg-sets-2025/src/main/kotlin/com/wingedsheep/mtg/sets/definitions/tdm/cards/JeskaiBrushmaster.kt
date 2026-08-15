package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jeskai Brushmaster — Tarkir: Dragonstorm #195
 * {1}{U}{R}{W} · Creature — Orc Monk · 2/4
 *
 * Double strike
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 *
 * Both abilities are plain keywords: [Keyword.DOUBLE_STRIKE] and the [prowess] helper.
 */
val JeskaiBrushmaster = card("Jeskai Brushmaster") {
    manaCost = "{1}{U}{R}{W}"
    colorIdentity = "URW"
    typeLine = "Creature — Orc Monk"
    power = 2
    toughness = 4
    oracleText = "Double strike\n" +
        "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"

    keywords(Keyword.DOUBLE_STRIKE)
    prowess()

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "Nino Vecia"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2eb06c36-cf7e-47a9-819e-adfc54284153.jpg?1743204763"
    }
}
