package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kjeldoran Skycaptain
 * {4}{W}
 * Creature — Human Soldier
 * 2/2
 *
 * Flying; first strike; banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)
 *
 * Three printed keywords and nothing else; all three are read by the combat rules.
 */
val KjeldoranSkycaptain = card("Kjeldoran Skycaptain") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Flying; first strike; banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.BANDING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Mark Poole"
        flavorText = "\"If we do our duty and uphold our honor, Kjeldor shall stand for a thousand years.\"\n—Arna Kennerüd, Skyknight"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf0115e0-6192-48a9-9e58-f3ef77ef77c2.jpg"
    }
}
