package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shield Bearer
 * {1}{W}
 * Creature — Human Soldier
 * 0/3
 *
 * Banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)
 *
 * Banding alone.
 */
val ShieldBearer = card("Shield Bearer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 0
    toughness = 3
    oracleText = "Banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    keywords(Keyword.BANDING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Dan Frazier"
        flavorText = "\"You have almost completed your four years, my son. Soon you shall be a Skyknight.\"\n—Arna Kennerüd, Skyknight"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/318ff2da-d309-469c-8e2f-fa3c7517a15a.jpg"
    }
}
