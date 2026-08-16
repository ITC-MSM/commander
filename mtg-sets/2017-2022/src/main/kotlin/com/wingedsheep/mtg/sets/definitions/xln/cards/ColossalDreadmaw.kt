package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Colossal Dreadmaw
 * {4}{G}{G}
 * Creature — Dinosaur
 * 6/6
 * Trample
 *
 * French vanilla — a single evergreen keyword, no other text.
 */
val ColossalDreadmaw = card("Colossal Dreadmaw") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    power = 6
    toughness = 6
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "Jesper Ejsing"
        flavorText = "If you feel the ground quake, run. If you hear its bellow, flee. If you see its teeth, it's too late."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76ac5b70-47db-4cdb-91e7-e5c18c42e516.jpg?1783935730"
    }
}
