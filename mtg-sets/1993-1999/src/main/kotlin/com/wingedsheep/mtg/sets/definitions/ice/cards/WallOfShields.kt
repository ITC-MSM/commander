package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Shields
 * {3}
 * Artifact Creature — Wall
 * 0/4
 *
 * Defender (This creature can't attack.)
 * Banding (If any creatures with banding you control are blocking a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by.)
 *
 * An artifact creature Wall with defender and banding; both are engine-live keywords, so the
 * card is its two keyword lines and nothing more.
 */
val WallOfShields = card("Wall of Shields") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Wall"
    power = 0
    toughness = 4
    oracleText = "Defender (This creature can't attack.)\n" +
        "Banding (If any creatures with banding you control are blocking a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by.)"

    keywords(Keyword.DEFENDER, Keyword.BANDING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "347"
        artist = "Randy Gallegos"
        flavorText = "\"It's the pokey bits that hurt the most.\"\n—Ib Halfheart, Goblin Tactician"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6376c7c4-aaca-4625-83d4-a49f01aec535.jpg"
    }
}
