package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rhox War Monk
 * {G}{W}{U}
 * Creature — Rhino Monk
 * 3 / 4
 * Lifelink
 *
 * A keyword-only creature: `keywords(Keyword.LIFELINK)` is the whole script, and the engine's
 * damage pipeline reads the keyword off projected state, so no ability wiring is required.
 */
val RhoxWarMonk = card("Rhox War Monk") {
    manaCost = "{G}{W}{U}"
    colorIdentity = "GUW"
    typeLine = "Creature — Rhino Monk"
    power = 3
    toughness = 4
    oracleText = "Lifelink"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Dan Dos Santos"
        flavorText = "Rhox monks are dedicated to spiritual growth and learning, and most bear the sigils of many students. However, they do not gladly suffer fools or those who disagree with their carefully wrought dogma."
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19fb67e1-b791-4246-aebc-49fcf0f92c6c.jpg"
    }
}
