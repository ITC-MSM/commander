package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wu Infantry
 * {1}{U}
 * Creature — Human Soldier
 * 2/1
 *
 * Vanilla — no rules text.
 */
val WuInfantry = card("Wu Infantry") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Xu Xiaoming"
        flavorText = "The first battle of Hefei was Sun Quan's last as a field general. From then on he let his generals command in the field while he directed battle from behind the front lines."
        imageUri = "https://cards.scryfall.io/normal/front/e/b/ebe4115e-7ca3-4996-a390-133c2e6d09b7.jpg?1783946119"
    }
}
