package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kjeldoran Warrior
 * {W}
 * Creature — Human Warrior
 * 1/1
 *
 * Banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)
 *
 * Banding alone. The reminder text is the keyword's, not a second ability.
 */
val KjeldoranWarrior = card("Kjeldoran Warrior") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior"
    power = 1
    toughness = 1
    oracleText = "Banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    keywords(Keyword.BANDING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Mark Poole"
        flavorText = "\"Give me a thousand such Warriors and I could change the world.\"\n—Avram Garrisson, Leader of the Knights of Stromgald"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce76f38f-566e-49ff-b197-510cfa1cb51c.jpg"
    }
}
