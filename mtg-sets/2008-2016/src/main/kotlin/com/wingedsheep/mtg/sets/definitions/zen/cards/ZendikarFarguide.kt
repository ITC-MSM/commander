package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zendikar Farguide
 * {4}{G}
 * Creature — Elemental
 * 3/3
 * Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)
 */
val ZendikarFarguide = card("Zendikar Farguide") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)"

    keywords(Keyword.FORESTWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "194"
        artist = "Vincent Proce"
        flavorText = "Expeditions follow the paths it leaves in its wake."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3aeb7fb-8618-4595-84b4-20881b824b3e.jpg"
    }
}
