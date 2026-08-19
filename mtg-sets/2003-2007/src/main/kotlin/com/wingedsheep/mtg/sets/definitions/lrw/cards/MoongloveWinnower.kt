package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moonglove Winnower
 * {3}{B}
 * Creature — Elf Rogue
 * 2/3
 * Deathtouch
 */
val MoongloveWinnower = card("Moonglove Winnower") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elf Rogue"
    power = 2
    toughness = 3
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "William O'Connor"
        flavorText = "Winnowers live to eliminate eyeblights, creatures the elves deem too ugly to exist."
        imageUri = "https://cards.scryfall.io/normal/front/e/f/effb7761-98a8-4cb8-883a-ddcb91d30c08.jpg?1783942886"
    }
}
