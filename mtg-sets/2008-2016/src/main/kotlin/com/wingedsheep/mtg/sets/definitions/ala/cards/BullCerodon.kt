package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bull Cerodon
 * {4}{R}{W}
 * Creature — Beast
 * 5 / 5
 * Vigilance, haste
 *
 * A vanilla-plus Naya beater: both printed words are engine-live simple keywords, so the whole card
 * is one `keywords` declaration with no script at all.
 */
val BullCerodon = card("Bull Cerodon") {
    manaCost = "{4}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Vigilance, haste"

    keywords(Keyword.VIGILANCE, Keyword.HASTE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Jesper Ejsing"
        flavorText = "It holds motionless vigil, watching Naya in silence through the screen of the whitecover. When it senses anything amiss, it launches forward with the uncanny sound of torn fog."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbae0fe2-5d52-434c-8ad1-4a5e42f4b7c4.jpg"
    }
}
