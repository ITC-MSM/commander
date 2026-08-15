package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vorstclaw
 * {4}{G}{G}
 * Creature — Elemental Horror
 * 7/7
 *
 * Vanilla — no rules text.
 */
val Vorstclaw = card("Vorstclaw") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental Horror"
    power = 7
    toughness = 7

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "201"
        artist = "Lucas Graciano"
        flavorText = "\"Where'd the werewolves go? Maybe *that* got hungry.\"\n—Halana of Ulvenwald"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/7591ee4f-9bfe-4419-84df-abf35d85bb94.jpg?1783940659"
    }
}
