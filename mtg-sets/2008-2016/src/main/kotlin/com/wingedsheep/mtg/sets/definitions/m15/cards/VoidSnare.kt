package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Void Snare
 * {U}
 * Sorcery
 * Return target nonland permanent to its owner's hand.
 */
val VoidSnare = card("Void Snare") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return target nonland permanent to its owner's hand."

    spell {
        val t = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Zack Stella"
        flavorText = "\"I've tried so many variations on how to get rid of annoying things that it's hard to decide which one I like best.\"\n—Ashurel, voidmage"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94efe426-0fb2-4a24-9b50-914e48105b57.jpg?1783939187"
    }
}
