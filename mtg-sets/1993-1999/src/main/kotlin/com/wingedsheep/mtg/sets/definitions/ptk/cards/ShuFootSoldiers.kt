package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shu Foot Soldiers
 * {2}{W}
 * Creature — Human Soldier
 * 2/3
 *
 * Vanilla — no rules text.
 */
val ShuFootSoldiers = card("Shu Foot Soldiers") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Xu Tan"
        flavorText = "Liu Bei lost many men at the battle of Runan because of his lack of strategy. It wasn't until he met Kongming that he began to truly succeed as a leader."
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd4268d5-f27b-44a5-91f6-6c90521825fd.jpg?1783946127"
    }
}
