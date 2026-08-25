package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Mountaineer
 * {R}
 * Creature — Goblin Scout
 * 1 / 1
 * Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)
 *
 * Landwalk is engine-live as a plain keyword: [Keyword.MOUNTAINWALK] via `keywords(...)` is read by
 * the block-legality rules against the defending player's battlefield, so no static ability or filter
 * has to be spelled out. Portal Second Age is this card's earliest real printing, hence the `p02`
 * package.
 */
val GoblinMountaineer = card("Goblin Mountaineer") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Scout"
    power = 1
    toughness = 1
    oracleText = "Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)"

    keywords(Keyword.MOUNTAINWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "DiTerlizzi"
        flavorText = "Goblin mountaineer, barely keeps his family fed."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f841fe25-237f-4303-b75e-392b82767eea.jpg"
    }
}
