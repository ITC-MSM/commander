package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Worldsoul Colossus
 * {X}{G}{W}
 * Creature — Elemental
 * 0/0
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * This creature enters with X +1/+1 counters on it.
 */
val WorldsoulColossus = card("Worldsoul Colossus") {
    manaCost = "{X}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elemental"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "This creature enters with X +1/+1 counters on it."
    power = 0
    toughness = 0

    keywords(Keyword.CONVOKE)
    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5186304-b0fb-44a6-ba61-0265b7f42da4.jpg?1783934117"
    }
}
