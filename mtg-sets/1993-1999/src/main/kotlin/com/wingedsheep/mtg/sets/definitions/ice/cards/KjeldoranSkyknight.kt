package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kjeldoran Skyknight
 * {2}{W}
 * Creature — Human Knight
 * 1/1
 *
 * Flying; first strike; banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)
 *
 * Three printed keywords and nothing else — the Skycaptain body one mana cheaper.
 */
val KjeldoranSkyknight = card("Kjeldoran Skyknight") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 1
    toughness = 1
    oracleText = "Flying; first strike; banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.BANDING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Mark Poole"
        flavorText = "\"My Aesthir is my most trusted ally. We fight as one and live as one, and we will die as one.\"\n—Arna Kennerüd, Skyknight"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f794665a-8353-482a-b065-2a0777a8acda.jpg"
    }
}
