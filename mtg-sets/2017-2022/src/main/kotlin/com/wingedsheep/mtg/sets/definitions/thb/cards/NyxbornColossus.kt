package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nyxborn Colossus
 * {3}{G}{G}{G}
 * Enchantment Creature — Giant
 * 6/7
 *
 * Vanilla — no rules text.
 */
val NyxbornColossus = card("Nyxborn Colossus") {
    manaCost = "{3}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Giant"
    power = 6
    toughness = 7

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Mathias Kollros"
        flavorText = "\"Tree-tall giants confronted her, fiercely demanding her tribute;\nFox-cunning Callaphe's slippery speaking entangled their senses...\"\n—*The Callapheia*"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b4f003c-1e99-4e53-ad6d-81ff3c592b2c.jpg?1783931531"
    }
}
