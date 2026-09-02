package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Introduction to Prophecy — Strixhaven: School of Mages #4 (canonical printing)
 * {3} · Sorcery — Lesson
 *
 * Scry 2, then draw a card.
 *
 * Printed order matters: [Effects.Scry] resolves first so the [Effects.DrawCards] that follows
 * takes the card the scry left on top. Lesson is only a subtype.
 */
val IntroductionToProphecy = card("Introduction to Prophecy") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Scry 2, then draw a card."

    spell {
        effect = Effects.Scry(2) then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Micah Epstein"
        flavorText = "Final grades are posted on the first day of class."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/7820923e-bad2-4d6a-92b3-97b9737d2ca9.jpg?1783927395"
    }
}
