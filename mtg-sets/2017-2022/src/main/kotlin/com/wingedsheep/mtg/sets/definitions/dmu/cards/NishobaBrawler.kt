package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nishoba Brawler
 * {1}{G}
 * Creature — Cat Warrior
 * * / 3
 * Trample
 * Domain — Nishoba Brawler's power is equal to the number of basic land types among lands you control.
 */
val NishobaBrawler = card("Nishoba Brawler") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Warrior"
    oracleText = "Trample\nDomain — Nishoba Brawler's power is equal to the number of basic land types among lands you control."

    // Domain is an ability word (CR 207.2c) — no rules meaning of its own. The
    // value is the number of basic land types among lands you control, capped at five.
    dynamicPower(DynamicAmounts.domain())
    toughness = 3

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Valera Lutfullina"
        flavorText = "Though native to cold climates, the nishoba have adapted to more temperate environments since the Ice Age ended."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2ef6cb5f-0ab3-4652-9b39-c2cbf6d693d5.jpg?1783921297"
    }
}
