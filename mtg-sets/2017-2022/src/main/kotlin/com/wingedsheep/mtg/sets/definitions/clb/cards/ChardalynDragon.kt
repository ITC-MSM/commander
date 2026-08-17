package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chardalyn Dragon
 * {6}
 * Artifact Creature — Dragon
 * 4/4
 * Flying
 *
 * A vanilla body with one evergreen keyword: [Keyword.FLYING] via `keywords(...)` is the whole card,
 * with no script at all.
 */
val ChardalynDragon = card("Chardalyn Dragon") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "306"
        artist = "Sergey Glushakov"
        flavorText = "First discovered in ancient Netheril, chardalyn stones absorb and hold magical energy, enabling constructs to be infused with fearsome power and unnatural malice."
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a950d8be-dcf7-4253-a3cc-c040ba632355.jpg?1783922678"
    }
}
