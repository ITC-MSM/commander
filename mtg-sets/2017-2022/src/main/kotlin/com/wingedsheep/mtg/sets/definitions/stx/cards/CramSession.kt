package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cram Session — Strixhaven: School of Mages #170 (canonical printing)
 * {1}{B/G} · Sorcery
 *
 * You gain 4 life.
 * Learn.
 *
 * One of the set's Witherbloom hybrid commons: `{B/G}` is payable with either black or green
 * (CR 202.2f), so the card is both black and green wherever colour is read.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val CramSession = card("Cram Session") {
    manaCost = "{1}{B/G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "You gain 4 life.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        effect = Effects.GainLife(4) then Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Marta Nael"
        flavorText = "The only thing more delicious than a top grade is Gyome's signature cake."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c59a249f-35ed-447a-845b-32ba5a53124e.jpg?1783927323"
    }
}
