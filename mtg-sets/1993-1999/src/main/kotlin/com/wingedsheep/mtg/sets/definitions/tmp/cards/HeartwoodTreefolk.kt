package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Heartwood Treefolk
 * {2}{G}{G}
 * Creature — Treefolk
 * 3/4
 * Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)
 */
val HeartwoodTreefolk = card("Heartwood Treefolk") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk"
    power = 3
    toughness = 4
    oracleText = "Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)"

    keywords(Keyword.FORESTWALK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Daren Bader"
        flavorText = "\"In all my years in Llanowar I never understood where trees fit in. They are revered by elves and watered on by dogs.\"\n" +
            "—Mirri of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de263f02-8e3e-4785-9c06-9adc168994f3.jpg"
    }
}
