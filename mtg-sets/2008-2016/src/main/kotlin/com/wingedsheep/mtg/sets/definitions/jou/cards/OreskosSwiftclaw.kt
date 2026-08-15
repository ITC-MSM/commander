package com.wingedsheep.mtg.sets.definitions.jou.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Oreskos Swiftclaw
 * {1}{W}
 * Creature — Cat Warrior
 * 3/1
 *
 * Vanilla — no rules text.
 */
val OreskosSwiftclaw = card("Oreskos Swiftclaw") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Warrior"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "James Ryman"
        flavorText = "After the Battle of Pharagax Bridge, the Champion spent many months among the leonin of Oreskos. She found that they were quick to take offense, not because they were thin-skinned, but because they were always eager for a fight.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03c67b91-7f05-44b1-9e99-3e2094434f6a.jpg?1783939456"
    }
}
